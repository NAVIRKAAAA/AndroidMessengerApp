package com.rhorbachevskyi.viewpager.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.utils.ApiServiceFactory
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUser
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUsers
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import kotlinx.coroutines.flow.MutableStateFlow

private val registerStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
private val authorizationStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
private val usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
private val users = MutableLiveData<List<Contact>>()
private var contactList = ArrayList<Contact>()
private val contactStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
private val states: ArrayList<Pair<Long, ApiStateUsers>> = ArrayList()
private val getStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
private val editUserStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)

object NetworkImplementation {
    suspend fun registerUser(body: UserRequest) {
        val apiService = ApiServiceFactory.createApiService()
        registerStateFlow.value = try {
            val response = UserRepository(apiService).registerUser(body)
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun authorizationUser(body: UserRequest) {
        val apiService = ApiServiceFactory.createApiService()
        authorizationStateFlow.value = try {
            UserRepository(apiService).authorizeUser(body).data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.not_correct_input)
        }
    }

    suspend fun autoLogin(context: Context) {
        val apiService = ApiServiceFactory.createApiService()
        authorizationStateFlow.value = try {
            val response = UserRepository(apiService).authorizeUser(
                UserRequest(
                    DataStore.getDataFromKey(
                        context,
                        Constants.KEY_EMAIL
                    ), DataStore.getDataFromKey(context, Constants.KEY_PASSWORD)
                )
            )
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.automatic_login_error)
        }
    }

    suspend fun getAllUsers(accessToken: String, user: UserData) {
        val apiService = ApiServiceFactory.createApiService()
        getContacts(user.id, accessToken)
        usersStateFlow.value = try {
            val response = UserRepository(apiService).getAllUsers("Bearer $accessToken")

            val filteredUsers =
                response.data.users?.filter {
                    it.name != null && it.email != user.email && !contactList.contains(
                        it.toContact()
                    )
                }
            users.postValue(filteredUsers?.map { it.toContact() } ?: emptyList())
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(userId: Long, accessToken: String) {
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
        usersStateFlow.value = try {
            val response =
                UserRepository(apiService).addContact(userId, "Bearer $accessToken", contact.id)
            states[states.size - 1] = Pair(contact.id, ApiStateUsers.Success(response.data.users))
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: Exception) {
            states[states.size - 1] =
                Pair(contact.id, ApiStateUsers.Error(R.string.invalid_request))
            ApiStateUsers.Error(R.string.invalid_request)
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
        getStateFlow.value = ApiStateUser.Loading
        val apiService = ApiServiceFactory.createApiService()
        getStateFlow.value = try {
            val response = UserRepository(apiService).getUser(userId, "Bearer $accessToken")
            response.data?.let { ApiStateUser.Success(it) } ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }
    }

    suspend fun editUser(user: UserData, accessToken: String) {
        editUserStateFlow.value = ApiStateUser.Loading
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
            editUserStateFlow.value = response.data?.let { ApiStateUser.Success(it) } ?: ApiStateUser.Error(
                R.string.invalid_request
            )
        } catch (e: Exception) {
            editUserStateFlow.value = ApiStateUser.Error(R.string.invalid_request)
        }

    }

    fun getStateRegister(): ApiStateUser = registerStateFlow.value
    fun getStateLogin(): ApiStateUser = authorizationStateFlow.value
    fun getStateAutoLogin(): ApiStateUser = authorizationStateFlow.value
    fun getStateUserAction(): ApiStateUsers = usersStateFlow.value
    fun getServerUsers(): List<Contact>? = users.value
    fun getStateAddContact(): ArrayList<Pair<Long, ApiStateUsers>> = states
    fun deleteStates() = states.clear()
    fun getContactList(): ArrayList<Contact> = contactList
    fun getStateContact(): ApiStateUsers = contactStateFlow.value
    fun getStateUser(): ApiStateUser = getStateFlow.value
    fun getStateEditUser(): ApiStateUser = editUserStateFlow.value
}