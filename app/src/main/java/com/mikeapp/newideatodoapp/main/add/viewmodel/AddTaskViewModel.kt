package com.mikeapp.newideatodoapp.main.add.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.newideatodoapp.data.LocationRepository
import com.mikeapp.newideatodoapp.data.TaskRepository
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.geo.GeoDomainManager
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
    private val locationRepository: LocationRepository,
    private val geoDomainManager: GeoDomainManager
) : ViewModel() {
    private var _locationUiState = MutableStateFlow(LocationUiState(emptyList()))

    val locationUiState: StateFlow<LocationUiState> = _locationUiState.asStateFlow()

    private var locationUiBefore: LocationUi? = null
    private var isLocationNotificationOnBefore: Boolean = false

    suspend fun getTask(taskId: Int): TaskEntity {
        return withContext(Dispatchers.IO) {
            val task = repository.getTask(taskId)
            return@withContext task
        }
    }

    fun pushLocation(locationUi: LocationUi?, isLocationNotificationOn: Boolean) {
        locationUiBefore = locationUi
        isLocationNotificationOnBefore = isLocationNotificationOn
    }

    fun popLocation(
        task: TaskEntity,
        locationUi: LocationUi?,
        isLocationNotificationOn: Boolean,
        onAdd: (TaskEntity, LocationUi) -> Unit,
        onDelete: (TaskEntity, LocationUi) -> Unit,
        onCompleted: () -> Unit
    ) {
        Log.d("bbbb", "Before: ${locationUiBefore?.name}, $isLocationNotificationOnBefore ==> popLocation: ${locationUi?.name}, $isLocationNotificationOn")
        when {
            locationUi != null && isLocationNotificationOn
                    && (locationUiBefore == null || !isLocationNotificationOnBefore) ->
                onAdd.invoke(task, locationUi)

            (locationUi == null || !isLocationNotificationOn)
                    && locationUiBefore != null && isLocationNotificationOnBefore ->
                onDelete.invoke(
                    task, locationUiBefore!!
                )

            isLocationNotificationOnBefore && isLocationNotificationOn
                    && locationUiBefore != null && locationUi != null
                    && locationUiBefore != locationUi -> {
                onAdd.invoke(task, locationUi)
                onDelete.invoke(task, locationUiBefore!!)
            }
        }
        locationUiBefore = null
        isLocationNotificationOnBefore = false
        onCompleted.invoke()
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

            popLocation(
                task = taskEntity,
                locationUi = location,
                isLocationNotificationOn = locationNotification,
                onAdd = { task, location ->
                    geoDomainManager.addGeoTask(task, location)
                },
                onDelete = { task, location ->
                    geoDomainManager.removeGeoTask(task, location)
                }
            ) {
                onCompleted.invoke()
            }
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

    suspend fun addLocation(locationUi: LocationUi): LocationUi {
        return withContext(Dispatchers.IO) {
            locationRepository.addLocation(locationUi)
            loadLocationList()
            val locationEntity = locationRepository.getLastLocationFromDb()
            return@withContext LocationUi(
                id = locationEntity.id,
                name = locationEntity.name,
                lat = locationEntity.lat,
                lon = locationEntity.lon,
                radius = locationEntity.radius
            )
        }
    }

    fun deleteLocation(locationUi: LocationUi) {
        viewModelScope.launch {
            locationUi.id?.let {
                locationRepository.deleteLocation(it)
                loadLocationList()
            }
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