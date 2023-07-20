package com.rhorbachevskyi.viewpager.domain.di.usermodule

import android.content.Context
import androidx.room.Room
import com.rhorbachevskyi.viewpager.data.database.UserDatabase
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserDaoModule {
    @Provides
    fun provideChannelDao(UserDatabase: UserDatabase): UserDao {
        return UserDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): UserDatabase {
        return Room.databaseBuilder(
            appContext,
            UserDatabase::class.java,
            "users_db"
        ).build()
    }
}