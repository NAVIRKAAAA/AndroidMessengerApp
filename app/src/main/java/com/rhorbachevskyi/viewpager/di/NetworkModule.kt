package com.rhorbachevskyi.viewpager.di

import com.rhorbachevskyi.viewpager.domain.network.ApiService
import com.rhorbachevskyi.viewpager.presentation.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule @Inject constructor() {

    @Provides
    fun providesRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun providesService(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }
}