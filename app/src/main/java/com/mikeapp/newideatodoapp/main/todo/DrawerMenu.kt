package com.mikeapp.newideatodoapp.main.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mikeapp.newideatodoapp.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun DrawerMenu(navController: NavController) {
    val viewModel: TodoViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Settings Icon
            IconButton(onClick = { /* Navigate to settings */ }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            }

            // About Icon
            IconButton(onClick = { /* Show about dialog or navigate to about screen */ }) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "About"
                )
            }

            // Design Icon
            IconButton(onClick = { navController.navigate("design") }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_color_lens_24),
                    contentDescription = "Colour"
                )
            }
        }

        uiState.lists.forEach {
            Text(it.name, modifier = Modifier.padding(8.dp))
        }
    }
}