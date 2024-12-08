package com.mikeapp.newideatodoapp.data.supabase.model

data class Task(
    val id: Int,
    val name: String,
    val completed: Boolean,
    val location: Int?,
    val priority: Int,
    val due: String?,
    val time: String?,
    val user: Int
)