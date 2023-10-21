package com.rhorbachevskyi.viewpager.di.usermodule

import com.rhorbachevskyi.viewpager.domain.network.UserApiService
import com.rhorbachevskyi.viewpager.domain.network.UsersRepository
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
    fun providesUsers(retrofit: Retrofit): UsersRepository {
        return retrofit.create(UsersRepository::class.java)
    }
}