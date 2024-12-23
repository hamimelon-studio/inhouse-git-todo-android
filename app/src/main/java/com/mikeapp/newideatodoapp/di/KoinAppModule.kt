package com.mikeapp.newideatodoapp.di

import androidx.room.Room
import com.mikeapp.newideatodoapp.data.GithubOpenApiRepository
import com.mikeapp.newideatodoapp.data.LocationRepository
import com.mikeapp.newideatodoapp.data.NetworkModule
import com.mikeapp.newideatodoapp.data.TaskRepository
import com.mikeapp.newideatodoapp.data.UserRepository
import com.mikeapp.newideatodoapp.data.github.GithubNetworkModule
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.geo.GeoDomainManager
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase
import com.mikeapp.newideatodoapp.login.viewmodel.LoginViewModel
import com.mikeapp.newideatodoapp.login.viewmodel.RegisterViewModel
import com.mikeapp.newideatodoapp.main.add.viewmodel.AddTaskViewModel
import com.mikeapp.newideatodoapp.main.todo.TodoViewModel
import com.mikeapp.newideatodoapp.util.SecurityUtil
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


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

    single { GeoDomainManager(get()) }
    single { SecurityUtil() }
    single { GithubOpenApiRepository(get()) }
    single { UserRepository(get(), get()) }
    single { TaskRepository(get(), get()) }
    single { LocationRepository(get(), get()) }
    single { GeofenceUseCase(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { TodoViewModel(get()) }
    viewModel { AddTaskViewModel(get(), get(), get()) }
}