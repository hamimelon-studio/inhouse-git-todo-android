package com.mikeapp.newideatodoapp.main.add.model

import androidx.annotation.DrawableRes
import com.mikeapp.newideatodoapp.R

enum class TaskFieldType {
    Date, Time, Priority, Location, List
}

data class TaskField(
    val type: TaskFieldType,
    val label: String,
    @DrawableRes val iconRes: Int
)

val taskFields = listOf(
    TaskField(TaskFieldType.Date, "Date", R.drawable.baseline_calendar_today_24),
    TaskField(TaskFieldType.Time, "Time", R.drawable.baseline_access_time_24),
    TaskField(TaskFieldType.Priority, "Priority", R.drawable.baseline_priority_high_24),
    TaskField(TaskFieldType.Location, "Location", R.drawable.baseline_location_pin_24),
    TaskField(TaskFieldType.List, "List", R.drawable.baseline_list_alt_24)
)