package com.rhorbachevskyi.viewpager.data.repositories

import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactService: ContactApiService,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val databaseImpl: DatabaseImpl,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getUsers(
        accessToken: String,
        user: UserData,
    ): ApiState {
        return try {
            val response = contactService.getUsers("${Constants.AUTH_PREFIX} $accessToken")
            if (response.data.users == null) return ApiState.Error(0)

            val serverContacts = UserDataHolder.serverContacts

            val filteredUsers = response.data.users.filter {
                it.name != null && it.email != user.email && !serverContacts.contains(it.toContact())
            }
            log("api $filteredUsers")
            // holder, database
            UserDataHolder.serverUsers = filteredUsers.map { it.toContact() }

            databaseImpl.addUsers(filteredUsers.map { it.toContact() })
            // result
            filteredUsers.let { ApiState.Success(it) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(
        userId: Long,
        accessToken: String
    ): ApiState {
        return try {
            val response =
                contactService.getUserContacts(userId, "${Constants.AUTH_PREFIX} $accessToken")
            if (response.data.contacts == null) return ApiState.Error(0)

            val users = response.data.contacts.map { it.toContact() }

            // holder, database
            UserDataHolder.serverContacts = response.data.contacts.map { it.toContact() }
            databaseImpl.addContacts(users)

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

    suspend fun getPagedUsers() { // : Flow<PagingData<UserData>>
//        val loader: UsersPageLoader<UserData> = { from, to ->
//            val response = getUsers(
//                UserDataHolder.userData.accessToken,
//                UserDataHolder.userData.user
//            )
//            when (response) {
//                is ApiState.Success<*> -> {
//                    response.data as List<UserData>
//                }
//
//                else -> emptyList()
//            }
//        }
//
//
//        val data = when (val response =
//            getUsers(UserDataHolder.userData.accessToken, UserDataHolder.userData.user)) {
//            is ApiState.Success<*> -> {
//                response.data as List<UserData>
//            }
//
//            else -> emptyList()
//        }
//        return Pager(
//            config = PagingConfig(
//                pageSize = Constants.PAGINATION_LIST_RANGE,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = {
//                ContactsPagingSource(
//                    loader,
//                    Constants.PAGINATION_LIST_RANGE,
//                    Pair(ListType.USERS, data)
//                )
//            }
//        ).flow
    }

    suspend fun getPagedContacts() { // : Flow<PagingData<Contact>>
//        val loader: UsersPageLoader<Contact> = { from, to ->
//            val response = getContacts(
//                UserDataHolder.userData.user.id,
//                UserDataHolder.userData.accessToken,
//            )
//            when (response) {
//                is ApiState.Success<*> -> {
//                    response.data as List<Contact>
//                }
//                else -> emptyList()
//            }
//        }
//
//
//        val data = when (val response =
//            getContacts(UserDataHolder.userData.user.id, UserDataHolder.userData.accessToken)) {
//            is ApiState.Success<*> -> {
//                response.data as List<Contact>
//            }
//
//            else -> emptyList()
//        }
//        return Pager(
//            config = PagingConfig(
//                pageSize = Constants.PAGINATION_LIST_RANGE,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = {
//                ContactsPagingSource(
//                    loader,
//                    Constants.PAGINATION_LIST_RANGE
//                )
//            }
//        ).flow
    }
}