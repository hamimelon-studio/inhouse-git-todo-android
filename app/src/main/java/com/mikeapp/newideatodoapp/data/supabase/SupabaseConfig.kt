package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.BuildConfig

object SupabaseConfig {
    const val API_URL = "https://${BuildConfig.SUPABASE_DOMAIN}.supabase.co"
    const val API_KEY = BuildConfig.SUPABASE_API_KEY
}