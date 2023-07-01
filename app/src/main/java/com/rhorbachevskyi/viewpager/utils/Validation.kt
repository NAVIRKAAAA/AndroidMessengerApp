package com.rhorbachevskyi.viewpager.utils

import android.util.Patterns



class Validation {

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        Regex(Constants.PASSWORD_REGEX).matches(password) && !password.contains(" ")
}