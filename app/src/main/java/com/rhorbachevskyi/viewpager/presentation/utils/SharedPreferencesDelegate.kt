package com.rhorbachevskyi.viewpager.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesDelegate(
    private val context: Context,
    private val name: String,
    private val defaultValue: String = ""
): ReadWriteProperty<Any?, String> {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(Constants.MY_PREFS_KEY, ComponentActivity.MODE_PRIVATE)
    }
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return sharedPreferences.getString(name, defaultValue)?:defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(name, value).apply()
    }
}
fun Fragment.sharedPreferences(name: String) = SharedPreferencesDelegate(requireContext(), name)
private fun Context.getSharedPrefs(): SharedPreferences =
    getSharedPreferences(Constants.MY_PREFS_KEY, Context.MODE_PRIVATE)
fun Context.saveToPrefs(key: String, value: String) {
    getSharedPrefs().edit().putString(key, value).apply()
}
fun Context.saveToPrefs(key: String, value: Long) {
    getSharedPrefs().edit().putLong(key, value).apply()
}
fun Context.getStringFromPrefs(key: String, defaultValue: String = ""): String =
    getSharedPrefs().getString(key, defaultValue).toString()
fun Context.getLongFromPrefs(key: String, defaultValue: Long = 0L): Long =
    getSharedPrefs().getLong(key, defaultValue)