package com.mikeapp.newideatodoapp.data.supabase.model

data class SupabaseList(
    val id: Int? = null,
    val name: String,
    val location: Int?,
    val sort: String?,
    val user: Int,
    val taskVersion: Long
)