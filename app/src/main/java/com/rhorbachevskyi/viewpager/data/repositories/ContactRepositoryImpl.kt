package com.rhorbachevskyi.viewpager.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.paging.ContactsPagingSource
import com.rhorbachevskyi.viewpager.paging.UsersPageLoader
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactService: ContactApiService,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val contactDatabaseRepository: ContactDatabaseRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getUsers(
        accessToken: String,
        user: UserData,
        from: Int = 0,
        to: Int = 0
    ): ApiState {
        return try {
            val response = contactService.getUsers("${Constants.AUTH_PREFIX} $accessToken")
            if (response.data.users == null) return ApiState.Error(0)

            val serverContacts = UserDataHolder.serverContacts
            val currentContacts = response.data.users.map { it.toContact() }

            val filteredUsers = response.data.users.filter {
                it.name != null && it.email != user.email && !serverContacts.contains(it.toContact())
            }
            val users = if (from == 0 && to == 0) {
                // holder, database
                UserDataHolder.serverUsers = filteredUsers.map { it.toContact() }
                userDatabaseRepository.addUsers(currentContacts.map { contact -> contact.toEntity() })
                filteredUsers
            } else {
                getUsersInRange(filteredUsers, from, to)
            }
            // result
            users.let { ApiState.Success(it) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(
        userId: Long,
        accessToken: String,
        from: Int = 0,
        to: Int = 0
    ): ApiState {
        return try {
            val response =
                contactService.getUserContacts(userId, "${Constants.AUTH_PREFIX} $accessToken")
            if (response.data.contacts == null) return ApiState.Error(0)

//            UserDataHolder.serverContacts = users
//            contactDatabaseRepository.deleteAllContacts()
//            contactDatabaseRepository.addContacts(users.map { contact -> contact.toEntity() })

            val users = if (from == 0 && to == 0) {
                // holder, database
                response.data.contacts.map { it.toContact() }
            } else {
                getUsersInRange(response.data.contacts, from, to)
            }

            users.let { ApiState.Success(it) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun addContact(userId: Long, contactId: Long, accessToken: String): ApiState {
        return try {
            val response =
                contactService.addContact(
                    userId,
                    "${Constants.AUTH_PREFIX} $accessToken",
                    contactId
                )
            UserDataHolder.states.add(contactId to ApiState.Success(response.data.users))
            response.data.let { ApiState.Success(it.users) }
        } catch (e: Exception) {
            UserDataHolder.states.add(
                contactId to ApiState.Error(R.string.invalid_request)
            )
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun deleteContact(userId: Long, accessToken: String, contactId: Long): ApiState {
        return try {
            val response = contactService.deleteContact(
                userId,
                contactId,
                "${Constants.AUTH_PREFIX} $accessToken",
            )
            response.data.let { ApiState.Success(it.users) }
        } catch (e: java.lang.Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun getPagedUsers(): Flow<PagingData<UserData>> {
        val loader: UsersPageLoader = { from, to ->
            val response = getUsers(
                UserDataHolder.userData.accessToken,
                UserDataHolder.userData.user,
                from,
                to
            )
            when (response) {
                is ApiState.Success<*> -> {
                    response.data as List<UserData>
                }

                else -> emptyList()
            }
        }


        val allUsers = when (val response =
            getUsers(UserDataHolder.userData.accessToken, UserDataHolder.userData.user)) {
            is ApiState.Success<*> -> {
                response.data as List<UserData>
            }

            else -> emptyList()
        }
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGINATION_LIST_RANGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContactsPagingSource(
                    loader,
                    Constants.PAGINATION_LIST_RANGE,
                    allUsers
                )
            }
        ).flow
    }

    suspend fun getPagedContacts(): Flow<PagingData<UserData>> {
        val loader: UsersPageLoader = { from, to ->
            val response = getContacts(
                UserDataHolder.userData.user.id,
                UserDataHolder.userData.accessToken
            )
            when (response) {
                is ApiState.Success<*> -> {
                    response.data as List<UserData>
                }

                else -> emptyList()
            }
        }


        val allUsers = when (val response =
            getUsers(UserDataHolder.userData.accessToken, UserDataHolder.userData.user)) {
            is ApiState.Success<*> -> {
                response.data as List<UserData>
            }

            else -> emptyList()
        }
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGINATION_LIST_RANGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContactsPagingSource(
                    loader,
                    Constants.PAGINATION_LIST_RANGE,
                    allUsers
                )
            }
        ).flow
    }

    private fun getUsersInRange(users: List<UserData>, from: Int, to: Int): List<UserData> =
        users.subList(from.coerceAtMost(users.size), to.coerceAtMost(users.size))
}