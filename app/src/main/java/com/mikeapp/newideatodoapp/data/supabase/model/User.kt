package com.mikeapp.newideatodoapp.data.supabase.model

data class User(
    val id: Int? = null,
    val created_at: String? = null,
    val userName: String,
    val passwordHash: String,
    val type: String
)