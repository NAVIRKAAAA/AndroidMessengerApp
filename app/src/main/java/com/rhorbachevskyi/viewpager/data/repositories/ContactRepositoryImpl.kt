package com.rhorbachevskyi.viewpager.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.UsersPagingLoader
import com.rhorbachevskyi.viewpager.data.UsersPagingSource
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import com.rhorbachevskyi.viewpager.domain.network.UsersRepository
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactService: ContactApiService,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val contactDatabaseRepository: ContactDatabaseRepository,
) : UsersRepository {
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
                    limit = pageIndex,
                    offset = offset
                )
            val contacts = UserDataHolder.serverContacts
            val filteredUsers =
                response.data.users?.filter {
                    it.name != null && it.email != user.email && !contacts.contains(it.toContact())
                }
            val users = filteredUsers?.map { it.toContact() } ?: emptyList()
            UserDataHolder.serverUsers = users
            userDatabaseRepository.addUsers(users.map { contact -> contact.toEntity() })
            response.data.let { ApiState.Success(it.users) }
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

    override fun getPagedUsers(accessToken: String, user: UserData): Flow<PagingData<Contact>> {

        val loader: UsersPagingLoader = { pageIndex, pageSize ->
            when (val usersResult = getUsers(accessToken, user, pageIndex, pageSize)) {
                is ApiState.Success<*> -> usersResult.data as List<Contact>
                else -> emptyList()
            }
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {UsersPagingSource(loader, PAGE_SIZE)}
        ).flow
    }
    companion object{
        const val PAGE_SIZE = 20
    }
}