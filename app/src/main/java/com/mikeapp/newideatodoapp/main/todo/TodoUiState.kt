package com.mikeapp.newideatodoapp.main.todo

import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity

data class TodoUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val currentList: ListEntity? = null,
    val lists: List<ListEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList()
)