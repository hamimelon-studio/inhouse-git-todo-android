package com.mikeapp.newideatodoapp.di

import androidx.room.Room
import com.mikeapp.newideatodoapp.data.GithubOpenApiRepository
import com.mikeapp.newideatodoapp.data.NetworkModule
import com.mikeapp.newideatodoapp.data.SupabaseRepository
import com.mikeapp.newideatodoapp.data.github.GithubNetworkModule
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.domain.UserLoginUseCase
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase
import com.mikeapp.newideatodoapp.login.viewmodel.LoginViewModel
import com.mikeapp.newideatodoapp.login.viewmodel.RegisterViewModel
import com.mikeapp.newideatodoapp.util.SecurityUtil
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel


val appModule = module {
    single { NetworkModule() }
    single { GithubNetworkModule() }
    single { SupabaseNetworkModule() }
    single {
        Room.databaseBuilder(
            get(),
            TnnDatabase::class.java,
            "tnn_db"
        ).build()
    }

    single { SecurityUtil() }
    single { GithubOpenApiRepository(get()) }
    single { SupabaseRepository(get(), get()) }
    single { UserLoginUseCase(get()) }
    single { GeofenceUseCase(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}