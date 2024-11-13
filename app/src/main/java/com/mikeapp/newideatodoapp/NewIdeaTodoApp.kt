package com.mikeapp.newideatodoapp

import android.app.Application
import com.mikeapp.newideatodoapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class NewIdeaTodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NewIdeaTodoApp)
            modules(appModule)
        }
    }
}