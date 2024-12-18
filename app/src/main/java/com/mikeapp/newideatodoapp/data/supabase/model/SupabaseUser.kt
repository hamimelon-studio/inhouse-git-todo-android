package com.mikeapp.newideatodoapp.data.supabase.model

data class SupabaseUser(
    val id: Int? = null,
    val created_at: String? = null,
    val userName: String,
    val passwordHash: String,
    val type: String,
    val email: String,
    val nickName: String,
    val defaultList: Int,
    val listVersion: Long,
    val locationVersion: Long
)