package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.firebase.MessagesRepository
import com.rhorbachevskyi.viewpager.data.model.Message
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AddContactUseCase
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactProfileViewModel @Inject constructor(
    private val addContactUseCase: AddContactUseCase,
    private val messagesRepository: MessagesRepository
) : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val usersState: StateFlow<ApiState> = _usersStateFlow

    private var alreadyAdded = false

    fun addContact(userId: Long, contactId: Long, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!hasInternet) {
                _usersStateFlow.value = ApiState.Error("немає інету")
                return@launch
            }
            if (alreadyAdded) return@launch
            alreadyAdded = true

            _usersStateFlow.value = ApiState.Loading
            _usersStateFlow.value = addContactUseCase(accessToken, userId, contactId)
        }
    fun sendMessage(receiverId: Long) {
        log("sendMessage")
        val message = Message(
            senderId = UserDataHolder.userData.user.id,
            receiverId = receiverId,
            text = "привіт, йоу",
            active = true
        )
        messagesRepository.addMessage(message)
    }

    fun changeState() {
        _usersStateFlow.value = ApiState.Initial
    }
}