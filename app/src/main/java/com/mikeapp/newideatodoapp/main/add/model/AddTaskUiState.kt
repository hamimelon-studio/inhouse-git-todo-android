package com.mikeapp.newideatodoapp.main.add.model

data class AddTaskUiState(
    val isLoading: Boolean = false,
    val taskName: String = "",
    val priority: String = "",
    val location: String = "",
    val date: String = ""
)