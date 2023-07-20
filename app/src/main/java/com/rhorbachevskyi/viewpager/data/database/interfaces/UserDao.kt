package com.rhorbachevskyi.viewpager.data.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity

@Dao
interface UserDao {
    @Update
    suspend fun updateUsers(users: List<ContactEntity>)

    @Query("SELECT * FROM users")
    suspend fun getUsers(): List<ContactEntity>

    @Insert
    suspend fun addUsers(users: List<ContactEntity>)
}