package com.rhorbachevskyi.viewpager.ui.fragments.auth

import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.domain.di.network.ApiService
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class AuthViewModel :
    ViewModel() { // TODO: register exception
    private val _registerStateFlow = MutableStateFlow<RegisterState>(RegisterState.Loading)
    val registerState: StateFlow<RegisterState> = _registerStateFlow
    fun isLogout() {
        _registerStateFlow.value = RegisterState.Loading
    }
    fun registerUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        val apiService: ApiService = retrofit.create(ApiService::class.java)

        try {
            val response = apiService.registerUser(body)
            _registerStateFlow.value =
                response.data?.let { RegisterState.Success(it) }
                    ?: RegisterState.Error(R.string.invalid_request)

        } catch (e: Exception) {
            _registerStateFlow.value = RegisterState.Error(
                R.string.register_error_user_exist
            )
        }
    }
    sealed class RegisterState {
        data class Success(val userData: UserData) : RegisterState()
        data class Error(val error: Int) : RegisterState()
        object Loading : RegisterState()
    }
}