package com.mikeapp.newideatodoapp.main.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.TaskRepository
import com.mikeapp.newideatodoapp.data.exception.AppException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TaskRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(TodoUiState())

    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val user = repository.getUser()
            val lists = repository.getLists()
            val tasks = repository.getTasks()
            val currentList = repository.getList(user.defaultList)
            _uiState.value = TodoUiState(
                userName = user.nickName,
                currentList = currentList,
                tasks = tasks,
                lists = lists
            )
        }
    }

    fun forceRefresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val user = repository.getUser()
                val lists = repository.getLists()
                val tasks = repository.getTasks()

                val currentList = repository.getList(user.defaultList)
                delay(400)
                _uiState.value = TodoUiState(
                    isLoading = false,
                    userName = user.nickName,
                    currentList = currentList,
                    tasks = tasks,
                    lists = lists
                )
            } catch (e: AppException) {
                delay(400)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}