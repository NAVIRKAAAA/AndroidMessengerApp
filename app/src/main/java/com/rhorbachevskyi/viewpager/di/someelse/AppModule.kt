package com.rhorbachevskyi.viewpager.di.someelse


import android.content.Context
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.bluetoothimpl.AndroidBluetoothController
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.interfaces.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }
}