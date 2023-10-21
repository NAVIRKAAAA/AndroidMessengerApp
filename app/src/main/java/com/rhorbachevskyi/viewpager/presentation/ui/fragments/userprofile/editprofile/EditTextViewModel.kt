package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.EditUserUseCase
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val editUserUseCase: EditUserUseCase
) : ViewModel() {
    private val _editUserStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val editUserState: StateFlow<ApiState> = _editUserStateFlow
    fun requestEditUser(
        userId: Long,
        accessToken: String,
        name: String,
        career: String? = null,
        phone: String,
        address: String? = null,
        date: Date? = null,
        hasInternet: Boolean,
        refreshToken: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _editUserStateFlow.value = ApiState.Loading
        if (!hasInternet) {
            _editUserStateFlow.value = ApiState.Error(R.string.No_internet_connection)
            return@launch
        }
        _editUserStateFlow.value =
            editUserUseCase(
                userId,
                accessToken,
                name,
                career,
                phone,
                address,
                date,
                refreshToken
            )
    }

    fun requestGetUser(): UserResponse.Data = UserDataHolder.userData

    fun isValidInputs(name: String, phone: String): Boolean =
        Validation.isValidUserName(name) && Validation.isValidMobilePhone(phone)

    fun isNotValidUserName(name: String): Boolean = !Validation.isValidUserName(name)

    fun isNotValidMobilePhone(phone: String): Boolean = !Validation.isValidMobilePhone(phone)
}