package com.rhorbachevskyi.viewpager.utils

import android.util.Patterns

private const val BIG_LETTER_REGEX = "[A-Z]"
private const val SMALL_LETTER_REGEX = "[a-z]"
private const val IS_HAVE_NUMBER_REGEX = "\\d"
private const val IS_SPACE = ' '

object Validation {

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 7 && password.contains(Regex(BIG_LETTER_REGEX)) &&
                password.contains(Regex(SMALL_LETTER_REGEX)) &&
                password.contains(Regex(IS_HAVE_NUMBER_REGEX)) && !password.contains(IS_SPACE)

    fun isValidUserName(username: String): Boolean = username.length >= 2
    fun isValidMobilePhone(phone: String):Boolean = phone.length >= 10
}