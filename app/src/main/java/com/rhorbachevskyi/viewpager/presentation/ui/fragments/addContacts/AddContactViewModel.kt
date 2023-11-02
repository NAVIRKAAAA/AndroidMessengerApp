package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
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
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AddContactUseCase
import com.rhorbachevskyi.viewpager.domain.usecases.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val allUsersUseCase: UsersUseCase,
    private val addContactUseCase: AddContactUseCase,
    private val databaseImpl: DatabaseImpl,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {
    private val _usersStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val usersState = _usersStateFlow.asStateFlow()

    private val _users = MutableStateFlow<List<Contact>>(listOf())
    val users: StateFlow<List<Contact>> = _users

    private val _states: MutableStateFlow<ArrayList<Pair<Long, ApiState>>> =
        MutableStateFlow(ArrayList())
    val states: StateFlow<ArrayList<Pair<Long, ApiState>>> = _states

    // so that it does not always depend on the server
    val supportList: ArrayList<Contact> = arrayListOf()

    var usersFlow: Flow<PagingData<Contact>> = MutableLiveData("").asFlow()
        .debounce(500)
        .flatMapLatest {
            databaseImpl.getPagedUsers()
        }
        .cachedIn(viewModelScope)

    fun getUsers(accessToken: String, user: UserData) =
        viewModelScope.launch(Dispatchers.Main) {
            _usersStateFlow.value = ApiState.Loading
            _usersStateFlow.value = allUsersUseCase(accessToken, user)
            _users.value = UserDataHolder.serverUsers
            databaseImpl.addUsersToSearchList(_users.value)
        }

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!supportList.contains(contact)) {
                supportList.add(contact)
                _states.value = arrayListOf(Pair(contact.id, ApiState.Loading))
                addContactUseCase(accessToken, userId, contact.id)
                _states.value = UserDataHolder.states
//                databaseImpl.deleteFromSearchList(contact)
            }
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