package com.mikeapp.newideatodoapp.main.todo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.TaskRepository
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
            val tasks = repository.getTasks()
            val lists = repository.getLists()
            val currentList = repository.getList(user.defaultList)
            _uiState.value = TodoUiState(
                userName = user.nickName,
                currentList = currentList,
                tasks = tasks,
                lists = lists
            )
        }
    }
}