package com.mikeapp.newideatodoapp.main.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(AddTaskUiState())

    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {

        }
    }

    fun saveTask(taskName: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            repository.addTask(taskName)
            onCompleted.invoke()
        }
    }
}