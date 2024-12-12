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

    fun load(taskId: Int? = null) {
        taskId?.let {
            viewModelScope.launch {
                val task = repository.getTask(taskId)
                _uiState.value = _uiState.value.copy(
                    taskName = task.name
                )
            }
        }
    }

    fun saveTask(taskName: String, taskId: Int? = null, onCompleted: () -> Unit) {
        viewModelScope.launch {
            if(taskId != null) {
                repository.updateTask(taskId, taskName)
            } else {
                repository.addTask(taskName)
            }
            onCompleted.invoke()
        }
    }
}