package com.mikeapp.newideatodoapp.di

import com.mikeapp.newideatodoapp.data.GithubOpenApiRepository
import com.mikeapp.newideatodoapp.domain.UserLoginUseCase
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase
import org.koin.dsl.module
import com.mikeapp.newideatodoapp.ui.theme.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel


val appModule = module {
    single { GithubOpenApiRepository() }
    single { UserLoginUseCase(get()) }
    single { GeofenceUseCase(get()) }
    viewModel { com.mikeapp.newideatodoapp.login.viewmodel.LoginViewModel() }
}