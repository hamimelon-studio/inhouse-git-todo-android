package com.mikeapp.newideatodoapp.main.add

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, paddingValues: PaddingValues, taskId: Int? = null) {
    val viewModel: AddTaskViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var newTaskTitle by remember { mutableStateOf("") }
    val context = LocalContext.current
    val rootView = (context as ComponentActivity).window.decorView
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var showPrioritySelection by remember { mutableStateOf(false) }
    var priority by remember { mutableIntStateOf(TaskPriority.Medium.value) }
    var showLocationSelection by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf<LocationUi?>(null) }

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
        viewModel.loadDraft()
        focusRequester.requestFocus()
    }

    LaunchedEffect(uiState.taskName) {
        newTaskTitle = uiState.taskName
    }

    Scaffold(
        topBar = {
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
                        viewModel.saveTask(newTaskTitle, taskId) {
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
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
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
            val iconModifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape)
                .padding(16.dp)
            val iconTint = MaterialTheme.colorScheme.onSecondaryContainer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = bottomPadding)
                    .align(Alignment.End)
                    .imePadding()
                    .windowInsetsPadding(WindowInsets.ime),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_calendar_today_24),
                    contentDescription = "Calendar",
                    tint = iconTint,
                    modifier = iconModifier.clickable { showDatePicker = true }
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
                    contentDescription = "Time",
                    tint = iconTint,
                    modifier = iconModifier.clickable { showTimePicker = true }
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_priority_high_24),
                    contentDescription = "Importance",
                    tint = iconTint,
                    modifier = iconModifier.clickable { showPrioritySelection = true }
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = iconTint,
                    modifier = iconModifier.clickable {
                        viewModel.saveDraft(taskName = newTaskTitle, taskId = taskId)
                        showLocationSelection = true
                    }
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_list_alt_24),
                    contentDescription = "List",
                    tint = iconTint,
                    modifier = iconModifier.clickable { }
                )
            }
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