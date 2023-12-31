package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor() : ViewModel() {
    fun getUser(): UserResponse.Data = UserDataHolder.userData

    fun toLogout(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        DataStore.deleteDataFromDataStore(context, Constants.KEY_EMAIL)
        DataStore.deleteDataFromDataStore(context, Constants.KEY_PASSWORD)
        DataStore.deleteDataFromDataStore(context, Constants.KEY_REMEMBER_ME)
    }
}