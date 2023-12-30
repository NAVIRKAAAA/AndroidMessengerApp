package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.EditUserUseCase
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val editUserUseCase: EditUserUseCase
) : ViewModel() {
    private val _editStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val editState = _editStateFlow.asStateFlow()
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
        _editStateFlow.value = ApiState.Loading
        if (!hasInternet) {
            _editStateFlow.value = ApiState.Error("немає інтернету")
            return@launch
        }
        _editStateFlow.value =
            editUserUseCase(
                accessToken,
                userId,
                name,
                career,
                phone,
                address,
                date,
                refreshToken
            )
    }

    fun isValidInputs(name: String, phone: String): Boolean =
        isValidUserName(name) && isValidMobilePhone(phone)

    fun isValidUserName(name: String): Boolean = Validation.isValidUserName(name)

    fun isValidMobilePhone(phone: String): Boolean = Validation.isValidMobilePhone(phone)
}