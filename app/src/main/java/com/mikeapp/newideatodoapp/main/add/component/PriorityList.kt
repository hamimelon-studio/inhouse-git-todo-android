package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            PriorityField(priority) {
                onSelected(it)
            }
        }
    }
}