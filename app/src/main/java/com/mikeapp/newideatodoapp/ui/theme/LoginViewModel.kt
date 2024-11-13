package com.mikeapp.newideatodoapp.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.domain.UserLoginUseCase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class LoginViewModel(val loginUseCase: UserLoginUseCase) : ViewModel() {
    fun login() {
        viewModelScope.launch(IO) {
            val list = loginUseCase.getUsers()
            list.forEach {
                Log.d("bbbb", it.toString())
            }
        }
    }
}