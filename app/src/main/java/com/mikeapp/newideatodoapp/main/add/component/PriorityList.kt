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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.data.enums.TaskPriority

@Composable
fun PriorityList(modifier: Modifier, onSelected: (Int) -> Unit) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val priorityList = listOf(
            TaskPriority.High,
            TaskPriority.Medium,
            TaskPriority.Low
        )
        items(priorityList) { priority ->
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
                                TaskPriority.High -> MaterialTheme.colorScheme.error
                                TaskPriority.Medium -> MaterialTheme.colorScheme.primary
                                TaskPriority.Low -> MaterialTheme.colorScheme.secondary
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
    }
}