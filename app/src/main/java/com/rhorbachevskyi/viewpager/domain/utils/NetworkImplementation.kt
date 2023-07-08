package com.rhorbachevskyi.viewpager.domain.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.repository.UserRepository
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStore
import com.rhorbachevskyi.viewpager.utils.ext.log
import kotlinx.coroutines.flow.MutableStateFlow


private val registerStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
private val authorizationStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
private val usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
private val users = MutableLiveData<List<Contact>>()
private var contactList = ArrayList<Contact>()
private val contactStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
private val states: ArrayList<Pair<Long, ApiStateUsers>> = ArrayList()
private val getStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
private val editUserStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)

object NetworkImplementation {
    suspend fun registerUser(body: UserRequest) {
        registerStateFlow.value = ApiState.Loading
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).registerUser(body)
            registerStateFlow.value =
                response.data?.let { ApiState.Success(it) }
                    ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            registerStateFlow.value = ApiState.Error(
                R.string.register_error_user_exist
            )
        }
    }

    suspend fun authorizationUser(body: UserRequest) {
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).authorizeUser(body)
            authorizationStateFlow.value =
                response.data?.let { ApiState.Success(it) }
                    ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            authorizationStateFlow.value = ApiState.Error(
                R.string.not_correct_input
            )
        }
    }

    suspend fun autoLogin(context: Context) {
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).authorizeUser(
                UserRequest(
                    DataStore.getDataFromKey(
                        context,
                        Constants.KEY_EMAIL
                    ), DataStore.getDataFromKey(context, Constants.KEY_PASSWORD)
                )
            )
            authorizationStateFlow.value =
                response.data?.let { ApiState.Success(it) }
                    ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            authorizationStateFlow.value = ApiState.Error(
                R.string.automatic_login_error
            )
        }
    }

    suspend fun getAllUsers(accessToken: String, user: UserData) {
        usersStateFlow.value = ApiStateUsers.Loading
        val apiService = ApiServiceFactory.createApiService()
        getContacts(user.id, accessToken)
        try {
            val response = UserRepository(apiService).getAllUsers("Bearer $accessToken")
            usersStateFlow.value = response.data.let { ApiStateUsers.Success(it.users) }

            val filteredUsers =
                response.data.users?.filter {
                    it.name != null && it.email != user.email && !contactList.contains(
                        it.toContact()
                    )
                }
            users.postValue(filteredUsers?.map { it.toContact() } ?: emptyList())
        } catch (e: Exception) {
            usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(userId: Long, accessToken: String) {
        contactStateFlow.value = ApiStateUsers.Initial
        val apiService = ApiServiceFactory.createApiService()
        contactList = try {
            val response =
                UserRepository(apiService).getUserContacts(userId, "Bearer $accessToken")
            contactStateFlow.value = response.data.let { ApiStateUsers.Success(it.contacts) }
            (response.data.contacts?.map { it.toContact() }
                ?: emptyList()) as ArrayList<Contact>
        } catch (e: Exception) {
            contactStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
            ArrayList()
        }
    }

    suspend fun addContact(userId: Long, contact: Contact, accessToken: String) {
        states.add(Pair(contact.id, ApiStateUsers.Loading))
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response =
                UserRepository(apiService).addContact(userId, "Bearer $accessToken", contact.id)
            usersStateFlow.value = response.data.let { ApiStateUsers.Success(it.users) }
            states[states.size - 1] = Pair(contact.id, ApiStateUsers.Success(response.data.users))
        } catch (e: Exception) {
            log(e.toString())
            usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
            states[states.size - 1] =
                Pair(contact.id, ApiStateUsers.Error(R.string.invalid_request))
        }

    }

    suspend fun deleteContact(userId: Long, accessToken: String, contactId: Long) {
        val apiService = ApiServiceFactory.createApiService()
        try {
            UserRepository(apiService).deleteContact(
                userId,
                "Bearer $accessToken",
                contactId
            )
        } catch (e: java.lang.Exception) {
            usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getUser(userId: Long, accessToken: String) {
        getStateFlow.value = ApiState.Loading
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).getUser(userId, "Bearer $accessToken")
            getStateFlow.value = response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            getStateFlow.value = ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun editUser(user: UserData, accessToken: String) {
        editUserStateFlow.value = ApiState.Loading
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).editUser(
                user.id,
                "Bearer $accessToken",
                user.name.toString(),
                user.career,
                user.phone.toString(),
                user.address,
                user.birthday
            )
            editUserStateFlow.value = response.data?.let { ApiState.Success(it) } ?: ApiState.Error(
                R.string.invalid_request
            )
        } catch (e: Exception) {
            editUserStateFlow.value = ApiState.Error(R.string.invalid_request)
        }

    }

    fun getStateRegister(): ApiState = registerStateFlow.value
    fun getStateLogin(): ApiState = authorizationStateFlow.value
    fun getStateAutoLogin(): ApiState = authorizationStateFlow.value
    fun getStateUserAction(): ApiStateUsers = usersStateFlow.value
    fun getServerUsers(): List<Contact>? = users.value
    fun getStateAddContact(): ArrayList<Pair<Long, ApiStateUsers>> = states
    fun deleteStates() = states.clear()
    fun getContactList(): ArrayList<Contact> = contactList
    fun getStateContact(): ApiStateUsers = contactStateFlow.value
    fun getStateUser(): ApiState = getStateFlow.value
    fun getStateEditUser(): ApiState = editUserStateFlow.value
}