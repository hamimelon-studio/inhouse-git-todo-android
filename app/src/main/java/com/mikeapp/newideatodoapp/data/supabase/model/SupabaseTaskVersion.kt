package com.mikeapp.newideatodoapp.data.supabase.model

data class SupabaseTaskVersion(
    val id: Int,
    val version: Long,
    val user: Int
)