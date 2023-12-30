package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact

import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.data.database.repositoriesimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AddContactUseCase
import com.rhorbachevskyi.viewpager.domain.usecases.ContactsUseCase
import com.rhorbachevskyi.viewpager.domain.usecases.DeleteContactUseCase
import com.rhorbachevskyi.viewpager.domain.usecases.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsUseCase: ContactsUseCase,
    private val addContactUseCase: AddContactUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val databaseImpl: DatabaseImpl,
    private val allUsersUseCase: UsersUseCase,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {

    private val _contactsStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val contactsStateFlow = _contactsStateFlow.asStateFlow()

    private val _contacts = MutableStateFlow(listOf<Contact>())
    val contacts = _contacts.asStateFlow()

    private val _selectContacts = MutableStateFlow<List<Contact>>(listOf())
    val selectContacts = _selectContacts.asStateFlow()

    private val _isMultiselect = MutableStateFlow(false)
    val isMultiselect = _isMultiselect.asStateFlow()

    private val _selectedData: MutableStateFlow<ArrayList<Pair<Boolean, Int>>> =
        MutableStateFlow(ArrayList())
    val selectedData = _selectedData.asStateFlow()

    // pagination

    var contactsFlow: Flow<PagingData<Contact>> = databaseImpl.getPagedContacts()

    fun getContactList(user: UserData, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!hasInternet) return@launch

            _contactsStateFlow.value = ApiState.Loading
            _contactsStateFlow.value = contactsUseCase(accessToken, user.id)
            allUsersUseCase(accessToken, user)
            databaseImpl.addUsers(UserDataHolder.serverUsers)
            _contacts.value = UserDataHolder.serverContacts
            databaseImpl.addUsersToSearchList(_contacts.value)
        }

    private fun addContact(
        userId: Long,
        contact: Contact,
        accessToken: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            _contactsStateFlow.value = ApiState.Loading
            _contactsStateFlow.value = addContactUseCase(accessToken, userId, contact.id)
            databaseImpl.addToSearchList(contact)
        }

    fun addContactToList(
        userId: Long,
        contact: Contact,
        accessToken: String,
        position: Int = _contacts.value.size,
    ): Boolean {
        val contactList = _contacts.value.toMutableList()

        return if (!contactList.contains(contact)) {
            contactList.add(position, contact)
            _contacts.value = contactList
            addContact(userId, contact, accessToken)
            true
        } else {
            false
        }
    }

    fun addSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value.toMutableList()

        return if (!contactList.contains(contact)) {
            contactList.add(contact)
            _selectContacts.value = contactList
            _selectedData.value.add(true to contact.id.toInt())
            true
        } else {
            false
        }
    }

    private fun deleteContact(
        userId: Long,
        accessToken: String,
        contact: Contact,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _contactsStateFlow.value = deleteContactUseCase(accessToken, userId, contact.id)
        databaseImpl.deleteFromSearchList(contact)
    }

    fun deleteContactFromList(
        userId: Long,
        accessToken: String,
        contact: Contact,
        hasInternet: Boolean
    ): Boolean {
        if (!hasInternet) {
            _contactsStateFlow.value = ApiState.Error("немає інету")
            return false
        }

        val contactList = _contacts.value.toMutableList()

        return if (contactList.contains(contact)) {
            deleteContact(userId, accessToken, contact)
            contactList.remove(contact)
            _contacts.value = contactList
            return true
        } else {
            false
        }
    }

    fun deleteSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value.toMutableList()

        return if (contactList.contains(contact)) {
            contactList.remove(contact)
            _selectContacts.value = contactList
            _selectedData.value.removeAt(_selectedData.value.indexOfFirst { it.second == contact.id.toInt() })
            true
        } else {
            false
        }
    }

    fun deleteSelectList(userId: Long, accessToken: String, hasInternet: Boolean): Boolean {
        if (!hasInternet) {
            _contactsStateFlow.value = ApiState.Error("немає інету")
            return false
        }

        val contactList = _selectContacts.value.toMutableList()

        for (contact in contactList) {
            deleteContactFromList(
                userId,
                accessToken,
                contact,
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
            _selectedData.value.clear()
        }
    }

    fun deleteStates() {
        UserDataHolder.states.clear()
    }

    fun changeState() {
        _contactsStateFlow.value = ApiState.Initial
    }

    @SuppressLint("MissingPermission")
    fun showNotification() {
        notificationManager.notify(1, notificationBuilder.build())
    }

    fun getContactPosition(contact: Contact): Int =
        contacts.value.indexOfFirst { it == contact }
}
