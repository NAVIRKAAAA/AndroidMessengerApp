package com.rhorbachevskyi.viewpager.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
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
        pageIndex: Int,
        pageSize: Int
    ): ApiState {
        return try {
            val offset = pageIndex * pageSize
            val response =
                contactService.getUsers(
                    accessToken = "${Constants.AUTHORIZATION_PREFIX} $accessToken",
                    pageSize,
                    offset
                )
            val contacts = UserDataHolder.serverContacts
            val filteredUsers =
                response.data.users?.filter {
                    it.name != null && it.email != user.email && !contacts.contains(it.toContact())
                }
            val users = filteredUsers?.map { it.toContact() } ?: emptyList()
            UserDataHolder.serverUsers = users
            userDatabaseRepository.addUsers(users.map { contact -> contact.toEntity() })
            filteredUsers?.takeLast(10)
            filteredUsers.let { ApiState.Success(it) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }
    suspend fun getContacts(userId: Long, accessToken: String): ApiState {
        return try {
            val response =
                contactService.getUserContacts(
                    userId,
                    "${Constants.AUTHORIZATION_PREFIX} $accessToken"
                )
            val users = response.data.contacts?.map { it.toContact() } ?: emptyList()
            UserDataHolder.serverContacts = users
            contactDatabaseRepository.deleteAllContacts()
            contactDatabaseRepository.addContacts(users.map { contact -> contact.toEntity() })
            response.data.let { ApiState.Success(it.contacts) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun addContact(userId: Long, contact: Contact, accessToken: String): ApiState {
        return try {
            val response =
                contactService.addContact(
                    userId,
                    "${Constants.AUTHORIZATION_PREFIX} $accessToken",
                    contact.id
                )
            UserDataHolder.states.add(contact.id to ApiState.Success(response.data.users))
            response.data.let { ApiState.Success(it.users) }
        } catch (e: Exception) {
            UserDataHolder.states.add(
                contact.id to ApiState.Error(R.string.invalid_request)
            )
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun deleteContact(userId: Long, accessToken: String, contact: Contact): ApiState {
        return try {
            val response = contactService.deleteContact(
                userId,
                contact.id,
                "${Constants.AUTHORIZATION_PREFIX} $accessToken",
            )
            response.data.let { ApiState.Success(it.users) }
        } catch (e: java.lang.Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    fun getPagedUsers(): Flow<PagingData<UserData>> {
        val loader: UsersPageLoader = { pageIndex, pageSize ->
            val response = getUsers(
                UserDataHolder.userData.accessToken,
                UserDataHolder.userData.user,
                pageIndex,
                pageSize
            )
            when (response) {
                is ApiState.Success<*> -> { response.data as List<UserData> }
                else -> emptyList()
            }
        }
        return Pager(
            config = PagingConfig(
                pageSize = 1
            ),
            pagingSourceFactory = { ContactsPagingSource(loader) }
        ).flow
    }


//    override fun getPageUsers(accessToken: String, user: UserData): Flow<PagingData<Contact>> {
//
//        val loader: UsersPagingLoader = { pageIndex, pageSize ->
//            when (val usersResult = getUsers(accessToken, user, pageIndex, pageSize)) {
//                is ApiState.Success<*> -> {
//                    if (usersResult.data is List<*>) usersResult.data.filterIsInstance<Contact>() else emptyList()
//                }
//                else -> emptyList()
//            }
//        }
//        return Pager(
//            config = PagingConfig(
//                pageSize = PAGE_SIZE,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { UsersPagingSource(loader, PAGE_SIZE) }
//        ).flow
//    }
}