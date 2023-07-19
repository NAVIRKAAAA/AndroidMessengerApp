package com.rhorbachevskyi.viewpager.domain.di

import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ContactModule {
    @Provides
    fun providesContactApiService(retrofit: Retrofit): ContactApiService {
        return retrofit.create(ContactApiService::class.java)
    }
}
