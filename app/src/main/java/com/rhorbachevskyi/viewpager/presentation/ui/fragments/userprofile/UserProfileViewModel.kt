package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.usecases.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val usersUseCase: UsersUseCase,
) : ViewModel() {
    fun getUser(): UserResponse.Data = UserDataHolder.userData

    fun getUserById(id: Int) = viewModelScope.launch {

    }
}

