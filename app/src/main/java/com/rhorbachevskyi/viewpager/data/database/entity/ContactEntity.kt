package com.rhorbachevskyi.viewpager.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class ContactEntity(
    @ColumnInfo(name = "contact_name")   val name: String? = null,
    @ColumnInfo(name = "contact_career")   val career: String? = null,
    @ColumnInfo(name = "contact_photo")   val photo: String? = null,
    @ColumnInfo(name = "contact_address")  val address: String? = null,
    @PrimaryKey val id: Long,
)


