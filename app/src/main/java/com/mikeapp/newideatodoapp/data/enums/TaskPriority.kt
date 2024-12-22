package com.mikeapp.newideatodoapp.data.enums

enum class TaskPriority(val value: Int) {
    High(2), Medium(1), Low(0)
}

fun getPriorityByValue(value: Int) : TaskPriority {
    return when (value) {
        TaskPriority.High.value -> TaskPriority.High
        TaskPriority.Medium.value -> TaskPriority.Medium
        TaskPriority.Low.value -> TaskPriority.Low
        else -> TaskPriority.Medium
    }
}