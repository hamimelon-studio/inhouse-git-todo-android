package com.mikeapp.newideatodoapp.home

import com.mikeapp.newideatodoapp.data.room.model.ListEntity

data class TodoUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val lists: List<ListEntity> = emptyList()
)