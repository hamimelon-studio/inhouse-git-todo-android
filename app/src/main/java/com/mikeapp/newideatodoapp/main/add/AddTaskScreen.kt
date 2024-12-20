package com.mikeapp.newideatodoapp.main.add

import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import com.google.android.gms.maps.model.LatLng
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import com.mikeapp.newideatodoapp.main.add.component.AddTaskTopBar
import com.mikeapp.newideatodoapp.main.add.component.CustomDatePickerDialog
import com.mikeapp.newideatodoapp.main.add.component.CustomTimePickerDialog
import com.mikeapp.newideatodoapp.main.add.component.LocationList
import com.mikeapp.newideatodoapp.main.add.component.PriorityList
import com.mikeapp.newideatodoapp.main.add.model.LocationUi
import com.mikeapp.newideatodoapp.main.add.viewmodel.AddTaskViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Composable
fun AddTaskScreen(navController: NavController, paddingValues: PaddingValues, taskId: Int? = null) {
    val viewModel: AddTaskViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var newTaskTitleText by rememberSaveable { mutableStateOf("") }
    var selectionStart by rememberSaveable { mutableIntStateOf(0) }
    var selectionEnd by rememberSaveable { mutableIntStateOf(0) }
    var newTaskTitleState = TextFieldValue(
        text = newTaskTitleText,
        selection = TextRange(selectionStart, selectionEnd)
    )

    val context = LocalContext.current
    val rootView = (context as ComponentActivity).window.decorView
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateTime by rememberSaveable { mutableStateOf<LocalDateTime?>(null) }
    var showPrioritySelection by remember { mutableStateOf(false) }
    var priority by rememberSaveable { mutableIntStateOf(TaskPriority.Medium.value) }
    var showLocationSelection by remember { mutableStateOf(false) }
    var location by rememberSaveable { mutableStateOf<LocationUi?>(null) }

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val locationResultName = navBackStackEntry?.savedStateHandle?.get<String>("location_name")
    val locationResultLatLng = navBackStackEntry?.savedStateHandle?.get<LatLng>("latLng")
    val locationResultRadius = navBackStackEntry?.savedStateHandle?.get<Double>("radius")

    if (locationResultName != null && locationResultLatLng != null && locationResultRadius != null) {
        Log.d(
            logTag,
            "locationResultName: $locationResultName, locationResultLatLng: $locationResultLatLng, locationResultRadius: $locationResultRadius"
        )
    }

    DisposableEffect(context) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            isKeyboardVisible = rect.bottom < rootView.height - 150
            true
        }
        rootView.viewTreeObserver.addOnPreDrawListener(listener)

        onDispose {
            rootView.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.load(taskId)
        focusRequester.requestFocus()
    }

    LaunchedEffect(uiState.taskName) {
        if (uiState.taskName.isNotEmpty() || newTaskTitleText.isEmpty()) {
            newTaskTitleText = uiState.taskName
            selectionStart = uiState.taskName.length
            selectionEnd = uiState.taskName.length
            newTaskTitleState =
                TextFieldValue(text = newTaskTitleText, selection = TextRange(selectionStart, selectionEnd))
        }
    }

    Scaffold(
        topBar = {
            AddTaskTopBar(navController, newTaskTitleState, taskId)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .weight(1f, fill = false)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                TextField(
                    value = newTaskTitleState,
                    onValueChange = {
                        newTaskTitleText = it.text
                        selectionStart = it.selection.start
                        selectionEnd = it.selection.end
                        newTaskTitleState = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .weight(1f)
                        .focusRequester(focusRequester),
                    maxLines = 5,
                    shape = MaterialTheme.shapes.small,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text("Add a new task") }
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )

            Chip(
                onClick = { },
                colors = ChipDefaults.chipColors(),
                border = ChipDefaults.chipBorder(),
                modifier = Modifier.padding(8.dp),
                content = { Text("Location: Home", style = MaterialTheme.typography.bodyMedium) }
            )

            if (showPrioritySelection) {
                PriorityList(Modifier.align(Alignment.Start)) {
                    priority = it
                    showPrioritySelection = false
                }
            }

            if (showLocationSelection) {
                LocationList(navController, Modifier.align(Alignment.Start)) {
                    location = it
                    showLocationSelection = false
                }
            }

            // Fixed position for icons at the bottom
            val bottomPadding by animateDpAsState(
                targetValue = if (isKeyboardVisible) 0.dp else 64.dp,
                animationSpec = tween(durationMillis = 300), label = ""
            )

        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                // Update or create LocalDateTime
                selectedDateTime = if (selectedDateTime != null) {
                    selectedDateTime!!.withYear(selectedDate.year)
                        .withMonth(selectedDate.monthValue)
                        .withDayOfMonth(selectedDate.dayOfMonth)
                } else {
                    selectedDate.atStartOfDay()
                }
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { selectedTime ->
                // Update or create LocalDateTime with selected time
                selectedDateTime = if (selectedDateTime != null) {
                    selectedDateTime!!.withHour(selectedTime.hour)
                        .withMinute(selectedTime.minute)
                } else {
                    LocalDateTime.now()
                        .withHour(selectedTime.hour)
                        .withMinute(selectedTime.minute)
                }
            }
        )
    }
}