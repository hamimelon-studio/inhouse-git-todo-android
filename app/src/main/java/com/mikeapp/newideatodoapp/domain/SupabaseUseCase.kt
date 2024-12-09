package com.mikeapp.newideatodoapp.domain

import android.util.Log
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class SupabaseUseCase {
    private val supabaseNetworkModule = get<SupabaseNetworkModule>(SupabaseNetworkModule::class.java)
    private val supabaseTaskApi = supabaseNetworkModule.supabaseTaskApi
    private val supabaseUserApi = supabaseNetworkModule.supabaseUserApi

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
                val supabaseUser = SupabaseUser(
                    userName = "mike",
                    passwordHash = "1231313",
                    type = "freeTierUser",
                    email = "william.johnson@example-pet-store.com"
                )
                supabaseUserApi.createUser(supabaseUser = supabaseUser)
            } catch (e: Exception) {
                Log.d("bbbb", "createUser: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}