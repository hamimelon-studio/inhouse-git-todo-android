package com.mikeapp.newideatodoapp.main.add.model

import com.mikeapp.newideatodoapp.data.enums.TaskPriority

data class AddTaskUiState(
    val isLoading: Boolean = false,
    val taskName: String = "",
    val priority: TaskPriority? = null,
    val location: LocationUi? = null,
    val date: String? = null
)