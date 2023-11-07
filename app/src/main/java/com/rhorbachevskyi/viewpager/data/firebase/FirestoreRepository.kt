package com.rhorbachevskyi.viewpager.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.rhorbachevskyi.viewpager.data.model.Message
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagesRepository {
    override fun addMessage(message: Message) {
        log("addMessage")
        val messagesCollection = firestore.collection("Messages")
        messagesCollection.add(message)
            .addOnSuccessListener { documentReference ->
                log("success")
            }
            .addOnFailureListener { e ->
                log("error: $e")
            }
    }
}