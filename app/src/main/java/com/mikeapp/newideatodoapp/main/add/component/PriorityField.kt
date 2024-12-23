package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import com.mikeapp.newideatodoapp.ui.theme.HighPriorityColor
import com.mikeapp.newideatodoapp.ui.theme.LowPriorityColor
import com.mikeapp.newideatodoapp.ui.theme.MediumPriorityColor

@Composable
fun PriorityField(priority: TaskPriority, onSelected: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelected(priority.value)
            }
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(48.dp)
                .background(
                    color = when (priority) {
                        TaskPriority.High -> HighPriorityColor
                        TaskPriority.Medium -> MediumPriorityColor
                        TaskPriority.Low -> LowPriorityColor
                    }
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = priority.name,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}