package com.mikeapp.newideatodoapp.domain

import android.util.Log
import com.mikeapp.newideatodoapp.data.NetworkModule.supabaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupabaseUseCase {
    fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = supabaseApi.getTask()
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
}