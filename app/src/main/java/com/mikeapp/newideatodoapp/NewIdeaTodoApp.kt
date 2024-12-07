package com.mikeapp.newideatodoapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.mikeapp.newideatodoapp.data.firebase.FirebaseUseCase
import com.mikeapp.newideatodoapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class NewIdeaTodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@NewIdeaTodoApp)
        FirebaseUseCase.firebase4()
        startKoin {
            androidContext(this@NewIdeaTodoApp)
            modules(appModule)
        }
    }
}