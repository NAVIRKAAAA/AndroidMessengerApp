package com.rhorbachevskyi.viewpager.domain.di.contactmodule

import android.content.Context
import androidx.room.Room
import com.rhorbachevskyi.viewpager.data.database.ContactDatabase
import com.rhorbachevskyi.viewpager.data.database.interfaces.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ContactDaoModule {
    @Provides
    fun provideChannelDao(contactDatabase: ContactDatabase): ContactDao {
        return contactDatabase.contactDao()
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ContactDatabase {
        return Room.databaseBuilder(
            appContext,
            ContactDatabase::class.java,
            "contacts"
        ).build()
    }
}