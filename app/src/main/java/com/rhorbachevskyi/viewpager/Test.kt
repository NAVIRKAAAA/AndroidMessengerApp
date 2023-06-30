package com.rhorbachevskyi.viewpager

import com.rhorbachevskyi.viewpager.domain.di.network.ApiService
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun main() {

    val logging = HttpLoggingInterceptor()

    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor(logging)

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://178.63.9.114:7777/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()
    val apiService: ApiService = retrofit.create(ApiService::class.java)

    val userRequest = UserRequest("test123123@email", "password123")

    val userResponse = runBlocking {
        apiService.registerUser(userRequest)
    }

    println(userResponse.message)
    if (userResponse.status == "success") {

    } else {
        println("Помилка реєстрації: ${userResponse.message}")
    }
}