package com.rhorbachevskyi.viewpager.domain.di.search

import android.content.Context
import androidx.room.Room
import com.rhorbachevskyi.viewpager.data.database.SearchDatabase
import com.rhorbachevskyi.viewpager.data.database.interfaces.SearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchDaoModule {
    @Provides
    fun provideChannelDao(searchDatabase: SearchDatabase): SearchDao {
        return searchDatabase.searchDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): SearchDatabase {
        return Room.databaseBuilder(
            appContext,
            SearchDatabase::class.java,
            "users"
        ).build()
    }
}