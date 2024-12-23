package com.mikeapp.newideatodoapp.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.UserRepository
import com.mikeapp.newideatodoapp.data.exception.AppException
import com.mikeapp.newideatodoapp.login.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun createAccount(userName: String, email: String, password: String, nickName: String, action: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        if (!validateUserName(userName)) return
        if (!validatePassword(password)) return
        if (!validateEmail(email)) return
        viewModelScope.launch {
            try {
                repository.createNewAccount(userName, email, password, nickName)
                _uiState.value = _uiState.value.copy(isLoading = false)
                action.invoke()
            } catch (e: AppException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userNameError = e.logMessage
                )
                Log.w(logTag, e.logMessage)
            }
        }
    }

    fun dismissUserNameError() {
        _uiState.value = _uiState.value.copy(userNameError = null)
    }

    fun dismissEmailError() {
        _uiState.value = _uiState.value.copy(emailError = null)
    }

    fun dismissPasswordError() {
        _uiState.value = _uiState.value.copy(passwordError = null)
    }

    fun dismissNickNameError() {
        _uiState.value = _uiState.value.copy(nickNameError = null)
    }

    private fun validateUserName(userName: String): Boolean {
        if (userName.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(isLoading = false, userNameError = "Username cannot be empty")
            return false
        }

        val usernameRegex = "^[a-zA-Z][a-zA-Z0-9_.-]*$".toRegex()
        if (!usernameRegex.matches(userName)) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    userNameError = "Invalid username. Please make sure user name starts with letters and only contains letters, numbers, dots, underscores, and hyphens"
                )
            return false
        }

        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(isLoading = false, passwordError = "Password cannot be empty")
            return false
        }

        if (password.trim().length < 6) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    passwordError = "It doesn't meet the minimum length requirements."
                )
            return false
        }

        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (email.trim().isEmpty()) {
            return true
        }

        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        if (!emailRegex.matches(email)) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    emailError = "Invalid email format. Please enter a valid email address."
                )
            return false
        }

        return true
    }
}