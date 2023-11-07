package com.rhorbachevskyi.viewpager.presentation.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.rhorbachevskyi.viewpager.data.firebase.MessagesRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.Message
import com.rhorbachevskyi.viewpager.domain.usecases.GetContactByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val getContactByIdUseCase: GetContactByIdUseCase,
) : ViewModel() {

    private val _messages: MutableLiveData<Message> = MutableLiveData()
    val messages = _messages

    init {
        observeMessages()
    }

    private fun observeMessages() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Messages")

        collectionRef.addSnapshotListener { snapshots, e ->

            for (dc in snapshots!!.documentChanges) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val addedData = dc.document.data
                    val gson = Gson()
                    val json = gson.toJson(addedData)
                    val message = gson.fromJson(json, Message::class.java)
                    _messages.value = message
                }
            }
        }
    }

    fun isMessageToMe(userId: Long, message: Message): Boolean {
        return userId == message.receiverId

    }

    fun getContactById(userId: Long): Contact {
        return getContactByIdUseCase(userId) ?: Contact()
    }
}

