package com.mikeapp.newideatodoapp.main.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.LocationRepository
import com.mikeapp.newideatodoapp.data.TaskRepository
import com.mikeapp.newideatodoapp.data.room.model.TaskDraftEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val repository: TaskRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddTaskUiState())

    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    private var _locationUiState = MutableStateFlow(LocationUiState(emptyList()))

    val locationUiState: StateFlow<LocationUiState> = _locationUiState.asStateFlow()

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
            if (taskId != null) {
                repository.updateTask(taskId, taskName)
            } else {
                repository.addTask(taskName)
            }
            onCompleted.invoke()
        }
    }

    fun loadLocationList() {
        viewModelScope.launch {
            val locations = locationRepository.getLocationList()
            val locationUiList = locations.map {
                LocationUi(
                    name = it.name,
                    lat = it.lat,
                    lon = it.lon,
                    radius = it.radius
                )
            }
            _locationUiState.value = LocationUiState(locationUiList)
        }
    }

    fun saveDraft(taskName: String, taskId: Int?) {
        viewModelScope.launch {
            repository.saveDraft(
                TaskDraftEntity(
                    taskId = taskId,
                    name = taskName,
                    completed = false,
                    location = null,
                    priority = 1,
                    due = null,
                    time = null,
                    list = 1
                )
            )
        }
    }

    fun loadDraft() {
        viewModelScope.launch {
            val draft = repository.getDraft()
            if (draft != null) {
                _uiState.value = _uiState.value.copy(
                    taskName = draft.name
                )
            }
        }
    }

    fun clearDraft() {
        viewModelScope.launch {
            repository.clearDraft()
        }
    }
}