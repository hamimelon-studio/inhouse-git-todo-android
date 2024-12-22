package com.mikeapp.newideatodoapp.main.add

import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.maps.model.LatLng
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import com.mikeapp.newideatodoapp.data.enums.getPriorityByValue
import com.mikeapp.newideatodoapp.main.add.component.AddTaskBottomIconRow
import com.mikeapp.newideatodoapp.main.add.component.AddTaskTopBar
import com.mikeapp.newideatodoapp.main.add.component.AttributeList
import com.mikeapp.newideatodoapp.main.add.component.CustomDatePickerDialog
import com.mikeapp.newideatodoapp.main.add.component.CustomTimePickerDialog
import com.mikeapp.newideatodoapp.main.add.component.LocationList
import com.mikeapp.newideatodoapp.main.add.component.PriorityList
import com.mikeapp.newideatodoapp.main.add.model.LocationUi
import com.mikeapp.newideatodoapp.main.add.model.TaskFieldType
import com.mikeapp.newideatodoapp.main.add.viewmodel.AddTaskViewModel
import com.mikeapp.newideatodoapp.util.RoomUtil.supportNullable
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AddTaskScreen(navController: NavController, paddingValues: PaddingValues, taskId: Int? = null) {
    val viewModel: AddTaskViewModel = koinViewModel()
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
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedTime by rememberSaveable { mutableStateOf<LocalTime?>(null) }
    var showPrioritySelection by remember { mutableStateOf(false) }
    var priority by rememberSaveable { mutableIntStateOf(TaskPriority.Medium.value) }
    var showLocationSelection by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf<LocationUi?>(null) }
    var locationId by rememberSaveable { mutableIntStateOf(-1) }
    var isShowAttribute by remember { mutableStateOf(true) }
    var isLocationNotificationOn by rememberSaveable { mutableStateOf(false) }
    var isDateTimeNotificationOn by rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val locationResultName = navBackStackEntry?.savedStateHandle?.get<String>("location_name")
    val locationResultLatLng = navBackStackEntry?.savedStateHandle?.get<LatLng>("latLng")
    val locationResultRadius = navBackStackEntry?.savedStateHandle?.get<Double>("radius")

    // Recovery Section
    if (locationResultName != null && locationResultLatLng != null && locationResultRadius != null) {
        viewModel.addLocation(
            LocationUi(
                name = locationResultName,
                lat = locationResultLatLng.latitude,
                lon = locationResultLatLng.longitude,
                radius = locationResultRadius
            )
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(taskId) {
        if (taskId != null) {
            val taskEntity = viewModel.getTask(taskId)
            newTaskTitleText = taskEntity.name
            selectionStart = taskEntity.name.length
            selectionEnd = taskEntity.name.length
            newTaskTitleState =
                TextFieldValue(text = newTaskTitleText, selection = TextRange(selectionStart, selectionEnd))
            priority = taskEntity.priority
            locationId = taskEntity.location ?: -1
            if (taskEntity.due?.supportNullable() != null) {
                selectedDate = LocalDate.parse(taskEntity.due)
            }
            if (taskEntity.time?.supportNullable() != null) {
                selectedTime = LocalTime.parse(taskEntity.time)
            }
            isLocationNotificationOn = taskEntity.locationNotification
            isDateTimeNotificationOn = taskEntity.dateTimeNotification
        }
    }

    LaunchedEffect(locationId, location) {
        if (locationId >= 0 && location == null) {
            location = viewModel.getLocationById(locationId)
        }
    }

    // Main Part Start
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

    Scaffold(
        topBar = {
            AddTaskTopBar(navController) {
                viewModel.saveTask(
                    taskName = newTaskTitleState.text,
                    taskId = taskId,
                    priority = getPriorityByValue(priority),
                    location = location,
                    date = selectedDate,
                    time = selectedTime,
                    locationNotification = isLocationNotificationOn,
                    dateTimeNotification = isDateTimeNotificationOn
                ) {
                    navController.navigate("todo")
                }
            }
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
                    .heightIn(min = 36.dp)
                    .weight(1f, fill = false)
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
                        .padding(8.dp)
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
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            if (isShowAttribute) {
                AttributeList(
                    priority = priority,
                    location = location,
                    dateTime = selectedTime?.let { time ->
                        selectedDate?.atTime(time)
                    } ?: selectedDate?.atStartOfDay(),
                    isLocationNotificationOn = isLocationNotificationOn,
                    isDateTimeNotificationOn = isDateTimeNotificationOn,
                    modifier = Modifier.align(Alignment.Start),
                    onLocationClicked = {
                        isLocationNotificationOn = !isLocationNotificationOn
                    },
                    onDateTimeClicked = {
                        isDateTimeNotificationOn = !isDateTimeNotificationOn
                    }
                )
            }

            if (showPrioritySelection) {
                PriorityList(Modifier.align(Alignment.Start)) {
                    priority = it
                    showPrioritySelection = false
                    isShowAttribute = true
                }
            }

            if (showLocationSelection) {
                LocationList(navController, Modifier.align(Alignment.Start)) {
                    location = it
                    locationId = it.id ?: -1
                    showLocationSelection = false
                    isShowAttribute = true
                }
            }

            // Fixed position for icons at the bottom
            val bottomPadding by animateDpAsState(
                targetValue = if (isKeyboardVisible) 0.dp else 64.dp,
                animationSpec = tween(durationMillis = 300), label = ""
            )

            AddTaskBottomIconRow(bottomPadding, Modifier.align(Alignment.End)) { fieldType ->
                when (fieldType) {
                    TaskFieldType.Date -> showDatePicker = true
                    TaskFieldType.Time -> showTimePicker = true
                    TaskFieldType.Priority -> {
                        showPrioritySelection = !showPrioritySelection
                        isShowAttribute = !showPrioritySelection
                        if (showPrioritySelection) {
                            showLocationSelection = false
                        }
                    }

                    TaskFieldType.Location -> {
                        showLocationSelection = !showLocationSelection
                        isShowAttribute = !showLocationSelection
                        if (showLocationSelection) {
                            showPrioritySelection = false
                        }
                    }

                    TaskFieldType.List -> {}
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                selectedDate = it
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = {
                selectedTime = it
                selectedDate = LocalDate.now()
            }
        )
    }

    BackHandler(onBack = {
        when {
            showPrioritySelection -> {
                showPrioritySelection = false
                isShowAttribute = true
            }

            showLocationSelection -> {
                showLocationSelection = false
                isShowAttribute = true
            }

            else -> {
                navController.popBackStack()
            }
        }
    })
}