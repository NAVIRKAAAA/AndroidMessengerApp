package com.rhorbachevskyi.viewpager.di.usermodule

import com.rhorbachevskyi.viewpager.domain.network.UserApiService
import com.rhorbachevskyi.viewpager.paging.alt.Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserModule {
    @Provides
    @Singleton
    fun providesUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesUsers(retrofit: Retrofit): Service {
        return retrofit.create(Service::class.java)
    }
}