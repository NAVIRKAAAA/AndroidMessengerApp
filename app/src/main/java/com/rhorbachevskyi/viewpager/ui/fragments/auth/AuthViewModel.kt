package com.rhorbachevskyi.viewpager.ui.fragments.auth

import android.service.autofill.UserData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.model.Users
import com.rhorbachevskyi.viewpager.domain.repository.UserRepository
import com.rhorbachevskyi.viewpager.utils.ext.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel constructor(private val contactsRepository: UserRepository) :
    ViewModel() {

    fun registerUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = contactsRepository.registerUser(body)
            log(response.toString())
        } catch (e: Exception) {
            log(e.toString())
        }
    }

    sealed class RegisterState {
        object Empty : RegisterState()
        data class Success(val userData: UserData) : RegisterState()
        data class Error(val error: Int) : RegisterState()
    }
}