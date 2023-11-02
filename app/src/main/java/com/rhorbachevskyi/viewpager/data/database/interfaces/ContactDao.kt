package com.rhorbachevskyi.viewpager.data.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addContacts(contacts: List<ContactEntity>)

    @Query(
        "SELECT * FROM users " +
                "LIMIT :limit OFFSET :offset"
    )
    suspend fun getContacts(limit: Int, offset: Int): List<ContactEntity>



    @Query("DELETE FROM users")
    suspend fun deleteAllContacts()
}