package com.rhorbachevskyi.viewpager.data.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
@Dao
interface ContactDao {
    @Insert
    suspend fun addContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}