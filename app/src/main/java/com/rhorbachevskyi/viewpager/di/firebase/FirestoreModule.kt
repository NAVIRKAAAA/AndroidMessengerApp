package com.rhorbachevskyi.viewpager.di.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rhorbachevskyi.viewpager.data.firebase.MessagesRepository
import com.rhorbachevskyi.viewpager.data.firebase.MessagesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FirestoreModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore {
         return Firebase.firestore
    }


    @Provides
    fun provideMessagesRepository(firestore: FirebaseFirestore): MessagesRepository {
        return MessagesRepositoryImpl(firestore)
    }
}
