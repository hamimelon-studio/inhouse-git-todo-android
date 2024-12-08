package com.mikeapp.newideatodoapp.domain

import android.util.Log
import com.mikeapp.newideatodoapp.data.NetworkModule.supabaseTaskApi
import com.mikeapp.newideatodoapp.data.NetworkModule.supabaseUserApi
import com.mikeapp.newideatodoapp.data.supabase.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupabaseUseCase {
    fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = supabaseTaskApi.getTask(user = 0)
                Log.d("bbbb", "test: ${tasks.size}")
                tasks.forEach {
                    Log.d("bbbb", "test: ${it.name}")
                }
            } catch (e: Exception) {
                Log.d("bbbb", "test: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun createUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = User(
                    created_at = System.currentTimeMillis().toString(),
                    userName = "mike",
                    passwordHash = "1231313",
                    type = "freeTierUser"
                )
                supabaseUserApi.createUser(user = user)
            } catch (e: Exception) {
                Log.d("bbbb", "createUser: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}