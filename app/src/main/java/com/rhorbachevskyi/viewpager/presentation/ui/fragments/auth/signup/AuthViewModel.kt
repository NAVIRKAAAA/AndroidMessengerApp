package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup

import androidx.lifecycle.ViewModel
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    fun isValidInputs(email: String, password: String): Boolean =
        Validation.isValidEmail(email) && Validation.isValidPassword(password)

    fun isNotValidEmail(email: String): Boolean =
        !Validation.isValidEmail(email) && email.isNotEmpty()

    fun isNotValidPassword(password: String): Boolean =
        !Validation.isValidPassword(password) && password.isNotEmpty()
}