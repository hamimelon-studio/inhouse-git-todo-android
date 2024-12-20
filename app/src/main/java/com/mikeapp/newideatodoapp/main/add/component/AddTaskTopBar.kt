package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.main.add.viewmodel.AddTaskViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskTopBar(navController: NavController, newTaskTitleState: TextFieldValue, taskId: Int?) {
    val viewModel: AddTaskViewModel = koinViewModel()
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                viewModel.clearDraft()
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = { Text("Save Task") },
        actions = {
            IconButton(onClick = {
                viewModel.saveTask(newTaskTitleState.text, taskId) {
                    viewModel.clearDraft()
                    navController.navigate("todo")
                }
            }) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}