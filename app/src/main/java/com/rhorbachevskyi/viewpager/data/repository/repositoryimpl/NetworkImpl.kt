package com.rhorbachevskyi.viewpager.data.repository.repositoryimpl


import android.content.Context
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.repository.ContactRepository
import com.rhorbachevskyi.viewpager.data.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.Constants.AUTHORIZATION_PREFIX
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date
import javax.inject.Inject

class NetworkImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val contactDatabaseRepository: ContactDatabaseRepository
) {
    suspend fun registerUser(body: UserRequest): ApiStateUser {
        return try {
            val response = userRepository.registerUser(body)
            response.data?.let { UserDataHolder.setUser(it) }
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun authorizeUser(body: UserRequest): ApiStateUser {
        return try {
            val response = userRepository.authorizeUser(body)
            response.data?.let { UserDataHolder.setUser(it) }
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.not_correct_input)
        }
    }

    suspend fun autoLogin(context: Context): ApiStateUser {
        return try {
            val response = userRepository.authorizeUser(
                UserRequest(
                    DataStore.getDataFromKey(
                        context,
                        Constants.KEY_EMAIL
                    ), DataStore.getDataFromKey(context, Constants.KEY_PASSWORD)
                )
            )
            response.data?.let { UserDataHolder.setUser(it) }
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.automatic_login_error)
        }
    }

    suspend fun getAllUsers(accessToken: String, user: UserData): ApiStateUsers {
        return try {
            val response = contactRepository.getAllUsers("$AUTHORIZATION_PREFIX $accessToken")
            val contacts = UserDataHolder.getContacts()
            val filteredUsers =
                response.data.users?.filter {
                    it.name != null && it.email != user.email && !contacts.contains(
                        it.toContact()
                    )
                }
            val users = MutableStateFlow(filteredUsers?.map { it.toContact() } ?: emptyList())
            UserDataHolder.setServerList(users)
            userDatabaseRepository.addUsers(users.value.map { contact -> contact.toEntity() })
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(userId: Long, accessToken: String): ApiStateUsers {
        return try {
            val response =
                contactRepository.getUserContacts(userId, "$AUTHORIZATION_PREFIX $accessToken")
            val users =
                MutableStateFlow(response.data.contacts?.map { it.toContact() } ?: emptyList())
            UserDataHolder.setContactList(users)
            contactDatabaseRepository.addContacts(users.value.map { contact -> contact.toEntity() })
            response.data.let { ApiStateUsers.Success(it.contacts) }
        } catch (e: Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun addContact(userId: Long, contact: Contact, accessToken: String): ApiStateUsers {
        return try {
            val response =
                contactRepository.addContact(
                    userId,
                    "$AUTHORIZATION_PREFIX $accessToken",
                    contact.id
                )
            UserDataHolder.setStates(Pair(contact.id, ApiStateUsers.Success(response.data.users)))
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: Exception) {
            UserDataHolder.setStates(
                Pair(
                    contact.id,
                    ApiStateUsers.Error(R.string.invalid_request)
                )
            )
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun deleteContact(userId: Long, accessToken: String, contact: Contact): ApiStateUsers {
        return try {
            val response = contactRepository.deleteContact(
                userId,
                "$AUTHORIZATION_PREFIX $accessToken",
                contact.id
            )
            response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: java.lang.Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun editUser(
        userId: Long,
        accessToken: String,
        name: String,
        career: String?,
        phone: String,
        address: String?,
        date: Date?,
        refreshToken: String
    ): ApiStateUser {
        return try {
            val response = userRepository.editUser(
                userId,
                "$AUTHORIZATION_PREFIX $accessToken",
                name,
                career,
                phone,
                address,
                date
            )
            response.data?.let {
                UserDataHolder.setUser(
                    UserResponse.Data(
                        it.user,
                        accessToken,
                        refreshToken
                    )
                )
            }
            response.data?.let { ApiStateUser.Success(it) } ?: ApiStateUser.Error(
                R.string.invalid_request
            )
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }

    }
}