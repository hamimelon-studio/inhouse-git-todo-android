package com.mikeapp.newideatodoapp.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.TaskRepository
import com.mikeapp.newideatodoapp.login.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TaskRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(TodoUiState())

    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val lists = repository.getLists()
            _uiState.value = TodoUiState(
                lists = lists ?: emptyList()
            )
        }
    }
}