package com.rhorbachevskyi.viewpager.utils

object Constants {
    // dialog
    const val DIALOG_TAG = "add_contact_dialog"

    // transition contact data
    const val TRANSITION_NAME_IMAGE = "TRANSITION_NAME_IMAGE"
    const val TRANSITION_NAME_CONTACT_NAME = "TRANSITION_NAME_FULL_NAME"
    const val TRANSITION_NAME_CAREER = "TRANSITION_NAME_CAREER"

    // data store
    const val REGISTER_DATA_STORE = "data store"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_REMEMBER_ME = "remember me"
    // ext log tag
    const val LOG_TAG = "LOG_TAG"

    // api base url
    const val BASE_URL = "http://178.63.9.114:7777/api/"

    // country mobile code
    const val MOBILE_CODE = "US"

    // screens in viewpager
    const val FRAGMENT_COUNT = 2

    // validation
    const val PASSWORD_REGEX =
        "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{7,}\$"

    // date
    const val OUTPUT_DATE_FORMAT = "dd/MM/yyyy"
    const val INPUT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy"
}