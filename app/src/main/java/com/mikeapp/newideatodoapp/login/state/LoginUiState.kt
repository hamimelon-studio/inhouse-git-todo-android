package com.mikeapp.newideatodoapp.login.state

data class LoginUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val passwordPlaceholder: String = "",
    val isRememberMe: Boolean = false,
    val userNameError: String? = null,
    val passwordError: String? = null,
)