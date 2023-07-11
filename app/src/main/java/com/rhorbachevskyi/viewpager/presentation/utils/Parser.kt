package com.rhorbachevskyi.viewpager.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Parser {
    fun getStringFromData(input: String): String = SimpleDateFormat(
        Constants.OUTPUT_DATE_FORMAT,
        Locale.ENGLISH
    ).format(SimpleDateFormat(Constants.INPUT_DATE_FORMAT, Locale.ENGLISH).parse(input)!!)
    fun getDataFromString(input: String) : Date? {
        return if (input.isNotBlank()) SimpleDateFormat(
            Constants.OUTPUT_DATE_FORMAT,
            Locale.getDefault()
        ).parse(input) else null
    }
}