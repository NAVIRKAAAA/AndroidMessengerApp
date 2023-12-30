package com.rhorbachevskyi.viewpager.data.repositoriesimpl.utils

object HandleError {
    fun getErrorMessage(code: Int): String {
        return when (code) {
            Error.BAD_REQUEST.code -> Error.BAD_REQUEST.message
            Error.UNAUTHORIZED.code -> Error.UNAUTHORIZED.message
            Error.FORBIDDEN.code -> Error.FORBIDDEN.message
            Error.NOT_FOUND.code -> Error.NOT_FOUND.message
            else -> "Невідома помилка (точніше - не скажу яка)"
        }
    }
    fun getErrorMessage(exception: Exception): String {
        return when (getCodeFromString(exception.toString())) {
            Error.BAD_REQUEST.code -> Error.BAD_REQUEST.message
            Error.UNAUTHORIZED.code -> Error.UNAUTHORIZED.message
            Error.FORBIDDEN.code -> Error.FORBIDDEN.message
            Error.NOT_FOUND.code -> Error.NOT_FOUND.message
            else -> "Невідома помилка (точніше - не скажу яка)"
        }
    }
    private fun getCodeFromString(input: String): Int {
        val pattern = Regex("""\b(?:400|401|402|404)\b""")
        val matchResult = pattern.find(input)

        return matchResult?.value?.toIntOrNull() ?: -1
    }
}

enum class Error(val code: Int, val message: String) {
    BAD_REQUEST(400, "Помилка запиту. Щось пішло не так (я не знаю)"),
    UNAUTHORIZED(401, "Недостатньо дозволів. Будь ласка, увійдіть для доступу"),
    FORBIDDEN(403, "Доступ заборонено. Вам не дозволено переглядати цей ресурс"),
    NOT_FOUND(404, "Не знайдено. Ресурс не існує або було видалено");
}

