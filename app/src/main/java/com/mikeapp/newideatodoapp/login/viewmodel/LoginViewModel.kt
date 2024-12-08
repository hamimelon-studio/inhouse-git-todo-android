package com.mikeapp.newideatodoapp.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.NetworkModule.supabaseUserApi
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun login() {
        viewModelScope.launch {
            val users = supabaseUserApi.getUser(userName = "eq.mike")
            Log.d("bbbb", "users.size: ${users.size}")
        }
    }
}