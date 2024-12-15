package com.mikeapp.newideatodoapp.main.add

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mikeapp.newideatodoapp.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationList(navController: NavController, modifier: Modifier, onSelected: (LocationUi) -> Unit) {
    val viewModel: AddTaskViewModel = koinViewModel()
    val uiState = viewModel.locationUiState.collectAsStateWithLifecycle().value
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionsGranted = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        permissionsGranted.value = permissionsResult.values.all { it }
        if (permissionsGranted.value) {
            navController.navigate("maps")
        }
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadLocationList()
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        uiState.locations.forEach { location ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onSelected(location)
                    }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_location_pin_24),
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${location.name} (radius: ${location.radius})",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    val allGranted = permissions.all { permission ->
                        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    }
                    if (allGranted) {
                        navController.navigate("maps")
                    } else {
                        launcher.launch(permissions)
                    }
                }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_add_24),
                contentDescription = "Create New Location",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Create new",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}