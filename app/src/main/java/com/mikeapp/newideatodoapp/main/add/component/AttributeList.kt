package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikeapp.newideatodoapp.main.add.viewmodel.AddTaskViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AttributeList(modifier: Modifier = Modifier) {
    val viewModel: AddTaskViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val priority = uiState.priority
    val location = uiState.location
    val date = uiState.date

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (priority != null) {
            PriorityField(priority) { }
        }

        if (location != null) {
            LocationField(location) { }
        }

//        if (date != null) {
//            PriorityField(priority) { }
//        }
    }
}