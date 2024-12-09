package com.mikeapp.newideatodoapp.login.state

data class RegisterUiState(
    val isLoading: Boolean = false,
    val userNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
)