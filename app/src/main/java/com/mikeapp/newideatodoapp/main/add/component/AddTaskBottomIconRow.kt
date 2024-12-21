package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.main.add.model.TaskFieldType
import com.mikeapp.newideatodoapp.main.add.model.taskFields

@Composable
fun AddTaskBottomIconRow(bottomPadding: Dp, modifier: Modifier, onAction: (TaskFieldType) -> Unit) {
    val iconModifier = Modifier
        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape)
        .padding(16.dp)
    val iconTint = MaterialTheme.colorScheme.onSecondaryContainer
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = bottomPadding)
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
                modifier = iconModifier.clickable { onAction(field.type) }
            )
        }
    }
}