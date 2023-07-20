package com.rhorbachevskyi.viewpager.domain.di

import android.content.Context
import androidx.room.Room
import com.rhorbachevskyi.viewpager.data.database.AppDatabase
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "users_db"
        ).build()
    }
}