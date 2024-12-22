package com.mikeapp.newideatodoapp.main.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.LocationRepository
import com.mikeapp.newideatodoapp.data.TaskRepository
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.main.add.model.LocationUi
import com.mikeapp.newideatodoapp.main.add.model.LocationUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime

class AddTaskViewModel(
    private val repository: TaskRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private var _locationUiState = MutableStateFlow(LocationUiState(emptyList()))

    val locationUiState: StateFlow<LocationUiState> = _locationUiState.asStateFlow()

    suspend fun getTask(taskId: Int): TaskEntity {
        return withContext(Dispatchers.IO) {
            val task = repository.getTask(taskId)
            return@withContext task
        }
    }

    fun saveTask(
        taskName: String,
        taskId: Int? = null,
        priority: TaskPriority,
        location: LocationUi?,
        date: LocalDate? = null,
        time: LocalTime? = null,
        locationNotification: Boolean = false,
        dateTimeNotification: Boolean = false,
        onCompleted: () -> Unit
    ) {
        viewModelScope.launch {
            val taskEntity = TaskEntity(
                id = taskId ?: -1,
                name = taskName,
                completed = false,
                location = location?.id,
                priority = priority.value,
                due = date?.toString(),
                time = time?.toString(),
                list = repository.getDefaultList().id,
                locationNotification = locationNotification,
                dateTimeNotification = dateTimeNotification
            )

            if (taskId != null) {
                repository.updateTask(taskEntity)
            } else {
                repository.addTask(taskEntity)
            }
            onCompleted.invoke()
        }
    }

    fun loadLocationList() {
        viewModelScope.launch {
            val locations = locationRepository.getLocationList()
            val locationUiList = locations.map {
                LocationUi(
                    id = it.id,
                    name = it.name,
                    lat = it.lat,
                    lon = it.lon,
                    radius = it.radius
                )
            }
            _locationUiState.value = LocationUiState(locationUiList)
        }
    }

    fun addLocation(locationUi: LocationUi) {
        viewModelScope.launch {
            locationRepository.addLocation(locationUi)
            loadLocationList()
        }
    }

    suspend fun getLocationById(locationId: Int): LocationUi {
        return withContext(Dispatchers.IO) {
            val locationEntity = locationRepository.getLocationById(locationId)
            return@withContext LocationUi(
                id = locationEntity.id,
                name = locationEntity.name,
                lat = locationEntity.lat,
                lon = locationEntity.lon,
                radius = locationEntity.radius
            )
        }
    }
}