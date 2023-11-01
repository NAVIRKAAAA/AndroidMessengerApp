package com.rhorbachevskyi.viewpager.data.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsers(users: List<ContactEntity>)

    @Update
    suspend fun updateUsers(users: List<ContactEntity>)

    @Query(
        "SELECT * FROM users " +
                "LIMIT :limit OFFSET :offset"
    )
    suspend fun getUsers(limit: Int, offset: Int): List<ContactEntity>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}