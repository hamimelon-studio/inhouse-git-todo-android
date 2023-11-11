package com.hongwei.demo.todo.ui.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hongwei.demo.todo.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    Text(text = "Home")
}