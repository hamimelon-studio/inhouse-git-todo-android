package com.mikeapp.newideatodoapp.geo

import com.mikeapp.newideatodoapp.main.add.model.LocationUi
import java.time.LocalDate
import java.time.LocalTime

data class GeoTask(
    val locationId: Int,
    val locationUi: LocationUi,
    val geoId: String,
    val taskId: Int,
    val listId: Int,
    val date: LocalDate?,
    val time: LocalTime?,
    val alarmedCount: Int = 0,
    val dismissedByUser: Boolean = false
)