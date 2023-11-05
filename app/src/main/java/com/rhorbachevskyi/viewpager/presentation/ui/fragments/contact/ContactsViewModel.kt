package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rhorbachevskyi.viewpager.data.database.repositoriesimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserResponse
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
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
    val contactsStateFlow: StateFlow<ApiState> = _contactsStateFlow

    private val _contactList = MutableStateFlow(listOf<Contact>())
    val contactList: StateFlow<List<Contact>> = _contactList

    private val _selectContacts = MutableStateFlow<List<Contact>>(listOf())
    val selectContacts: StateFlow<List<Contact>> = _selectContacts

    private val _isMultiselect = MutableStateFlow(false)
    val isMultiselect = _isMultiselect

    private val _isSelectItem: MutableStateFlow<ArrayList<Pair<Boolean, Int>>> =
        MutableStateFlow(ArrayList())
    val isSelectItem: StateFlow<ArrayList<Pair<Boolean, Int>>> = _isSelectItem

    // pagination

    var contactsFlow: Flow<PagingData<Contact>> = MutableLiveData("").asFlow()
        .debounce(500)
        .flatMapLatest {
            databaseImpl.getPagedContacts()
        }
        .cachedIn(viewModelScope)

    fun getContactList(user: UserData, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!hasInternet) return@launch

            _contactsStateFlow.value = ApiState.Loading
            _contactsStateFlow.value = contactsUseCase(accessToken, user.id)
            allUsersUseCase(accessToken, user)
            databaseImpl.addUsers(UserDataHolder.serverUsers)
            _contactList.value = UserDataHolder.serverContacts
            databaseImpl.addUsersToSearchList(_contactList.value)
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
            _isSelectItem.value.add(true to contact.id.toInt())
            return true
        }

        return false
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
        val contactList = _contactList.value.toMutableList()

        if (!hasInternet) {
            _contactsStateFlow.value = ApiState.Error("not has internet hahaha")
            return false
        }
        if (contactList.contains(contact)) {
            deleteContact(userId, accessToken, contact)
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
            _contactsStateFlow.value = ApiState.Error("not has internet hahaha")
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
            _isSelectItem.value.clear()
        }
    }

    fun deleteStates() {
        UserDataHolder.states.clear()
    }

    fun changeState() {
        _contactsStateFlow.value = ApiState.Initial
    }

    @SuppressLint("MissingPermission")
    fun showNotification(context: Context) {
        notificationManager.notify(1, notificationBuilder.build())
    }

    fun requestGetUser(): UserResponse.Data = UserDataHolder.userData
    fun getContactPosition(contact: Contact): Int =
        contactList.value.indexOfFirst { it == contact }
}
