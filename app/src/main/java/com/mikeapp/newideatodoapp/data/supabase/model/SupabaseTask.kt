package com.mikeapp.newideatodoapp.data.supabase.model

data class SupabaseTask(
    val id: Int? = null,
    val name: String,
    val completed: Boolean,
    val location: Int?,
    val priority: Int,
    val due: String?,
    val time: String?,
    val list: Int,
    val locationNotification: Boolean,
    val dateTimeNotification: Boolean,
)