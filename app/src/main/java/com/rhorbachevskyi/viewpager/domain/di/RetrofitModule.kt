package com.rhorbachevskyi.viewpager.domain.di

import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule{
    @Provides
    @Singleton
    fun providesRetrofit(factory: retrofit2.Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(factory)
            .build()
    }

    @Provides
    @Singleton
    fun providesGsonConverterFactory(): retrofit2.Converter.Factory {
        return GsonConverterFactory.create()
    }
}