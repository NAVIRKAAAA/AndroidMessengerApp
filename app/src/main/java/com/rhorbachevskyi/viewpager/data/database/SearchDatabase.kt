package com.rhorbachevskyi.viewpager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.SearchDao
import com.rhorbachevskyi.viewpager.data.database.utils.DateTypeConverter

@Database(entities = [ContactEntity::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class SearchDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
}
