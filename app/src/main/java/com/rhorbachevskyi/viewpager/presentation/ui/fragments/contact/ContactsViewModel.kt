package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val networkImpl: NetworkImpl,
    private val databaseImpl: DatabaseImpl,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _contactList = MutableStateFlow(listOf<Contact>())
    val contactList: StateFlow<List<Contact>> = _contactList

    private val _selectContacts = MutableStateFlow<List<Contact>>(listOf())
    val selectContacts: StateFlow<List<Contact>> = _selectContacts
    private val _isMultiselect = MutableStateFlow(false)
    val isMultiselect = _isMultiselect

    private val _isSelectItem: MutableStateFlow<ArrayList<Pair<Boolean, Int>>> =
        MutableStateFlow(ArrayList())
    val isSelectItem: StateFlow<ArrayList<Pair<Boolean, Int>>> = _isSelectItem

    fun initialContactList(userId: Long, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value = if (hasInternet) {
                networkImpl.getContacts(userId, accessToken)
            } else {
                databaseImpl.getAllContacts()
            }
            _contactList.value = UserDataHolder.getContacts()
            databaseImpl.addUsersToSearchList(_contactList.value)
        }

    private fun addContact(
        userId: Long,
        contact: Contact,
        accessToken: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value = networkImpl.addContact(userId, contact, accessToken)
            databaseImpl.addToSearchList(contact)
        }

    fun addContactToList(
        userId: Long,
        contact: Contact,
        accessToken: String,
        position: Int = _contactList.value.size,
    ): Boolean {
        val contactList = _contactList.value.toMutableList()


        if (!contactList.contains(contact)) {
            contactList.add(position, contact)
            _contactList.value = contactList
            addContact(userId, contact, accessToken)
            return true
        }

        return false
    }

    fun addSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value.toMutableList()

        if (!contactList.contains(contact)) {
            contactList.add(contact)
            _selectContacts.value = contactList
            _isSelectItem.value.add(Pair(true, contact.id.toInt()))
            return true
        }

        return false
    }

    private fun deleteContact(
        userId: Long,
        accessToken: String,
        contact: Contact,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = networkImpl.deleteContact(userId, accessToken, contact)
        databaseImpl.deleteFromSearchList(contact)
    }

    fun deleteContactFromList(
        userId: Long,
        accessToken: String,
        contactId: Long,
        hasInternet: Boolean
    ): Boolean {
        val contact = _contactList.value.find { it.id == contactId }
        val contactList = _contactList.value.toMutableList()

        if (!hasInternet) {
            _usersStateFlow.value = ApiStateUsers.Error(R.string.No_internet_connection)
            return false
        }
        if (contactList.contains(contact)) {
            deleteContact(userId, accessToken, contact!!)
            contactList.remove(contact)
            _contactList.value = contactList
            return true
        }
        return false
    }

    fun deleteSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value.toMutableList()
        if (contactList.contains(contact)) {
            contactList.remove(contact)
            _selectContacts.value = contactList
            _isSelectItem.value.removeAt(_isSelectItem.value.indexOfFirst { it.second == contact.id.toInt() })
            return true
        }

        return false
    }

    fun deleteSelectList(userId: Long, accessToken: String, hasInternet: Boolean): Boolean {
        if (!hasInternet) {
            _usersStateFlow.value = ApiStateUsers.Error(R.string.No_internet_connection)
            return false
        }

        val contactList = _selectContacts.value.toMutableList()
        for (contact in contactList) {
            deleteContactFromList(
                userId,
                accessToken,
                contact.id,
                true
            )
            deleteSelectContact(contact)
        }

        _selectContacts.value = contactList
        return true
    }


    fun changeMultiselectMode() {
        _isMultiselect.value = !_isMultiselect.value
        if (!isMultiselect.value) {
            _selectContacts.value = emptyList()
            _isSelectItem.value.clear()
        }
    }

    fun deleteStates() {
        UserDataHolder.deleteStates()
    }

    fun changeState() {
        _usersStateFlow.value = ApiStateUsers.Initial
    }

    fun showNotification(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1, notificationBuilder.build())
        }
    }
}