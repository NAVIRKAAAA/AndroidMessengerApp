package com.rhorbachevskyi.viewpager.data.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addList(contacts: List<ContactEntity>)

    @Query("DELETE FROM users")
    suspend fun deleteUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(contact: ContactEntity)

    @Delete
    suspend fun deleteUser(contact: ContactEntity)

    @Query("SELECT * FROM users")
    suspend fun getList(): List<ContactEntity>
}