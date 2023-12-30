package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AddContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactProfileViewModel @Inject constructor(
    private val addContactUseCase: AddContactUseCase
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val stateFlow = _stateFlow.asStateFlow()

    private var alreadyAdded = false

    fun addContact(userId: Long, contactId: Long, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!hasInternet) {
                _stateFlow.value = ApiState.Error("немає інету")
                return@launch
            }
            if (alreadyAdded) return@launch
            alreadyAdded = true

            _stateFlow.value = ApiState.Loading
            _stateFlow.value = addContactUseCase(accessToken, userId, contactId)
        }

    fun changeState() {
        _stateFlow.value = ApiState.Initial
    }
}