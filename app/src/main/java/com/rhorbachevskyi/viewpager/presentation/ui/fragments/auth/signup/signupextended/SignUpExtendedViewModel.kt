package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup.signupextended

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.RegisterUserUseCase
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import com.rhorbachevskyi.viewpager.presentation.utils.Parser
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpExtendedViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    private val _registerStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val registerState: StateFlow<ApiState> = _registerStateFlow
    fun isLogout() {
        _registerStateFlow.value = ApiState.Initial
    }

    fun registerUser(email: String, password: String, name: String, phone: String) = viewModelScope.launch(Dispatchers.IO) {
        log("$email $password $name $phone")
        _registerStateFlow.value = ApiState.Loading
        _registerStateFlow.value = registerUserUseCase(email, password, name, phone)
    }

    fun isNotValidUserName(name: String): Boolean = !Validation.isValidUserName(name)

    fun isNotValidMobilePhone(phone: String): Boolean = !Validation.isValidMobilePhone(phone)

    fun saveUserDataToDataStore(context: Context, email: String, password: String) =
        viewModelScope.launch(Dispatchers.IO) {
            DataStore.saveData(context, email, password)
        }

    fun getNameFromEmail(email: String) : String = Parser.parsingEmail(email)
}