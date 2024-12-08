package com.mikeapp.newideatodoapp.data.supabase.model

data class User(
    val id: Int,
    val created_at: String,
    val userName: String,
    val passwordHash: Boolean,
    val type: String
)