package com.mikeapp.newideatodoapp.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.UserRepository
import com.mikeapp.newideatodoapp.data.exception.AppException
import com.mikeapp.newideatodoapp.data.room.model.UserEntity
import com.mikeapp.newideatodoapp.login.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(userName: String, password: String, isRememberMe: Boolean, action: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        if (!validateUserName(userName)) return
        if (!validatePassword(password)) return
        viewModelScope.launch {
            try {
                repository.authenticateUser(userName, password, isRememberMe)
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

    fun dismissPasswordError() {
        _uiState.value = _uiState.value.copy(passwordError = null)
    }

    private fun validateUserName(userName: String): Boolean {
        if (userName.trim().isEmpty()) {
            _uiState.value =
                _uiState.value.copy(isLoading = false, userNameError = "Username cannot be empty")
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
            _uiState.value =
                _uiState.value.copy(isLoading = false, passwordError = "Password cannot be empty")
            return false
        }

        return true
    }

    suspend fun loadLocalLoginInfo(): UserEntity? {
        val localUser = repository.retrieveLocalUser()
        _uiState.value = localUser?.run {
            LoginUiState(
                userName = userName,
                passwordPlaceholder = "placeholder",
                isRememberMe = rememberMe
            )
        } ?: LoginUiState(
            isAutoLoggingIn = false,
            userName = "",
            passwordPlaceholder = "",
            isRememberMe = true
        )
        return localUser
    }

    fun autoLogin(localUser: UserEntity? = null, action: () -> Unit) {
        viewModelScope.launch {
            if (localUser != null && localUser.rememberMe) {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val user =
                    repository.authenticateUserHash(localUser.userName, localUser.passwordHash)
                if (user == null) {
                    Log.w(logTag, "auto log in failed, get null user name from repository call.")
                    _uiState.value = _uiState.value.copy(isAutoLoggingIn = false, isLoading = false)
                } else {
                    // log in success
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    Log.d("bbbb", "user: $user")
                    action.invoke()
                }
            }
        }
    }
}