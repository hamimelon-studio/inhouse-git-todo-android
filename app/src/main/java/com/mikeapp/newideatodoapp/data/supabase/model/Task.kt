package com.mikeapp.newideatodoapp.data.supabase.model

data class Task(
    val id: Int,
    val name: String,
    val completed: Boolean
)