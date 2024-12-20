package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.main.add.model.TaskField
import com.mikeapp.newideatodoapp.main.add.model.TaskFieldType
import com.mikeapp.newideatodoapp.main.add.model.taskFields

@Composable
fun AddTaskBottomIconRow(bottomPadding: Dp, onAction: (TaskFieldType) -> Unit) {
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
        taskFields.forEach { field ->
            Icon(
                imageVector = ImageVector.vectorResource(id = field.iconRes),
                contentDescription = field.label,
                tint = iconTint,
                modifier = iconModifier.clickable { }
            )
        }
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
            modifier = iconModifier.clickable { showPrioritySelection = !showPrioritySelection }
        )
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = iconTint,
            modifier = iconModifier.clickable {
                viewModel.saveDraft(taskName = newTaskTitleState.text, taskId = taskId)
                showLocationSelection = !showLocationSelection
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