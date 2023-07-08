package com.rhorbachevskyi.viewpager.utils

import android.util.Patterns

object Validation {

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        Regex(Constants.PASSWORD_REGEX).matches(password) && !password.contains(" ")

    fun isValidUserName(username: String): Boolean = username.length >= 2
    fun isValidMobilePhone(phone: String):Boolean = phone.length >= 10

}