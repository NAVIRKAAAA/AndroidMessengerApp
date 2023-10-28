package com.rhorbachevskyi.viewpager.data.database.repository

import android.nfc.tech.MifareUltralight
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import javax.inject.Inject

class UserDatabaseRepository @Inject constructor(

) {

    suspend fun addUsers(users: List<ContactEntity>) {

    }

    private suspend fun deleteAllContacts() {

    }


}