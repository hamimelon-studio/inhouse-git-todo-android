package com.mikeapp.newideatodoapp.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun TodoScreen(navController: NavController, paddingValues: PaddingValues) {
    val viewModel: TodoViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Column {
        Text("Todo Screen")

        Text("list size: ${uiState.lists.size}")
    }
}