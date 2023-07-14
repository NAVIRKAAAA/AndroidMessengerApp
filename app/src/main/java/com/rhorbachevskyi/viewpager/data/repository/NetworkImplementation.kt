package com.rhorbachevskyi.viewpager.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.Constants.AUTHORIZATION_PREFIX
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

private val users = MutableLiveData<List<Contact>>()
private var contactList = ArrayList<Contact>()
private val contactStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
private val states: ArrayList<Pair<Long, ApiStateUsers>> = ArrayList()

class NetworkImplementation @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun registerUser(body: UserRequest) : ApiStateUser {
        return try {
            val response = userRepository.registerUser(body)
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun authorizationUser(body: UserRequest) : ApiStateUser {
        return try {
            userRepository.authorizeUser(body).data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.not_correct_input)
        }
    }

    suspend fun autoLogin(context: Context) : ApiStateUser {
        return try {
            val response = userRepository.authorizeUser(
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

    suspend fun getAllUsers(accessToken: String, user: UserData) : ApiStateUsers {
        contactList = getContacts(user.id, accessToken)
        return try {
            val response = userRepository.getAllUsers("$AUTHORIZATION_PREFIX $accessToken")

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

    suspend fun getContacts(userId: Long, accessToken: String) : ArrayList<Contact> {
        return try {
            val response =
                userRepository.getUserContacts(userId, "$AUTHORIZATION_PREFIX $accessToken")
            contactStateFlow.value = response.data.let { ApiStateUsers.Success(it.contacts) }
            (response.data.contacts?.map { it.toContact() }
                ?: emptyList()) as ArrayList<Contact>
        } catch (e: Exception) {
            contactStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
            ArrayList()
        }
    }

    suspend fun addContact(userId: Long, contact: Contact, accessToken: String) : ApiStateUsers {
        states.add(Pair(contact.id, ApiStateUsers.Loading))
        return try {
            val response =
                userRepository.addContact(userId, "$AUTHORIZATION_PREFIX $accessToken", contact.id)
            states[states.size - 1] = Pair(contact.id, ApiStateUsers.Success(response.data.users))
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: Exception) {
            states[states.size - 1] =
                Pair(contact.id, ApiStateUsers.Error(R.string.invalid_request))
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun deleteContact(userId: Long, accessToken: String, contactId: Long):ApiStateUsers {
        return try {
            val response = userRepository.deleteContact(
                userId,
                "$AUTHORIZATION_PREFIX $accessToken",
                contactId
            )
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: java.lang.Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getUser(userId: Long, accessToken: String) : ApiStateUser {
        return try {
            val response = userRepository.getUser(userId, "$AUTHORIZATION_PREFIX $accessToken")
            response.data?.let { ApiStateUser.Success(it) } ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }
    }

    suspend fun editUser(user: UserData, accessToken: String) : ApiStateUser {
        return try {
            val response = userRepository.editUser(
                user.id,
                "$AUTHORIZATION_PREFIX $accessToken",
                user.name.toString(),
                user.career,
                user.phone.toString(),
                user.address,
                user.birthday
            )
             response.data?.let { ApiStateUser.Success(it) } ?: ApiStateUser.Error(
                R.string.invalid_request
            )
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }

    }

    fun getServerUsers(): List<Contact>? = users.value
    fun getStateAddContact(): ArrayList<Pair<Long, ApiStateUsers>> = states
    fun deleteStates() = states.clear()
    fun getStateContact(): ApiStateUsers = contactStateFlow.value
}