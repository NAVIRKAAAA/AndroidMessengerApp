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
import com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.repositories.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AddContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val addContactUseCase: AddContactUseCase,
    private val databaseImpl: DatabaseImpl,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat,
    private val contactRepository: ContactRepositoryImpl
) : ViewModel() {
//    private val _usersStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
//    val usersState: StateFlow<ApiState> = _usersStateFlow

    private val _users = MutableStateFlow<List<Contact>>(listOf())
    val users: StateFlow<List<Contact>> = _users


    private val _states: MutableStateFlow<ArrayList<Pair<Long, ApiState>>> =
        MutableStateFlow(ArrayList())
    val states: StateFlow<ArrayList<Pair<Long, ApiState>>> = _states

    // so that it does not always depend on the server
    val supportList: ArrayList<Contact> = arrayListOf()

    val loading = MutableLiveData<Boolean>()
    val usersList: Flow<PagingData<UserData>>
    private val searchBy = MutableLiveData("")
    init {
        usersList = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                contactRepository.getPagedUsers()
            }
            .cachedIn(viewModelScope)
    }

//    val usersList = Pager(PagingConfig(20)) {
//        ContactsPagingSource(contactRepository)
//    }.flow.cachedIn(viewModelScope)

    fun getUsers(accessToken: String, user: UserData, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.Main) {
//            log("start getUsers()")
//            val pager = Pager(PagingConfig(20)) {
//                ContactsPagingSource(usersRepository)
//            }
//            usersList = pager.flow.cachedIn(viewModelScope)
//            log("end getUsers()")
        }


//    fun addContact(userId: Long, contact: Contact, accessToken: String, hasInternet: Boolean) =
//        viewModelScope.launch(Dispatchers.IO) {
//            if (!supportList.contains(contact)) {
//                supportList.add(contact)
//                _states.value = arrayListOf(contact.id to ApiState.Loading)
//                if (hasInternet) {
//                    addContactUseCase(userId, contact, accessToken)
//                } else {
//                    //_usersStateFlow.value = ApiState.Error(R.string.No_internet_connection)
//                }
//                _states.value = UserDataHolder.states
//                databaseImpl.deleteFromSearchList(contact)
//            }
//        }


    fun changeState() {
        // _usersStateFlow.value = ApiState.Initial
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

    fun requestGetUser(): UserResponse.Data = UserDataHolder.userData
}