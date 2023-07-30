package com.rhorbachevskyi.viewpager.presentation.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.first


object DataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.REGISTER_DATA_STORE)

    suspend fun getDataFromKey(context: Context, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return if (preferences.contains(dataStoreKey)) {
            preferences[dataStoreKey]
        } else {
            null
        }
    }

    suspend fun putData(context: Context, key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun deleteDataFromDataStore(context: Context, key: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings.remove(dataStoreKey)
        }
    }

    suspend fun saveData(requireContext: Context, email: String, password: String) {
        putData(requireContext, Constants.KEY_EMAIL, email)
        putData(requireContext, Constants.KEY_PASSWORD, password)
        putData(requireContext, Constants.KEY_REMEMBER_ME, Constants.KEY_REMEMBER_ME)
    }
}
