package com.mikeapp.newideatodoapp

import android.app.Application
import android.content.pm.PackageManager
import android.content.pm.SigningInfo
import android.os.Build
import android.util.Base64
import android.util.Log
import com.google.firebase.FirebaseApp
import com.mikeapp.newideatodoapp.data.firebase.FirebaseUseCase
import com.mikeapp.newideatodoapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.security.MessageDigest
import java.security.Signature

class NewIdeaTodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@NewIdeaTodoApp)
        FirebaseUseCase.firebase4()
        startKoin {
            androidContext(this@NewIdeaTodoApp)
            modules(appModule)
        }
        checkSign()
    }

    private fun checkSign() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signingInfo: SigningInfo? = packageInfo.signingInfo

            if (signingInfo == null) {
                Log.d("bbbb", "signingInfo is null")
            }

            val signatures: Array<out android.content.pm.Signature>? =
                if (signingInfo!!.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners // For APKs with multiple signers
                } else {
                    signingInfo.signingCertificateHistory // For older APKs with a single signer
                }

            signatures!!.forEach { signature ->
                val sha1 = MessageDigest.getInstance("SHA-1").digest(signature.toByteArray())
                val sha1String = sha1.joinToString(":") { byte -> "%02X".format(byte) }
                Log.d("bbbb", "SHA-1: $sha1String")
                Log.d("bbbb", "Base64: ${Base64.encodeToString(signature.toByteArray(), Base64.DEFAULT)}")
            }
        }
    }
}