package com.mikeapp.newideatodoapp.main.todo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.data.enums.TaskPriority
import com.mikeapp.newideatodoapp.data.enums.getPriorityByValue
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.ui.theme.HighPriorityColor
import com.mikeapp.newideatodoapp.ui.theme.LowPriorityColor
import com.mikeapp.newideatodoapp.ui.theme.MediumPriorityColor

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun TodoItemCardWithSwipe(
    navController: NavController,
    todo: TaskEntity,
    onComplete: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 200.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1, sizePx to 2)
    val swipeOffset = swipeableState.offset.value
    val swipeAreaColor: MutableState<Color?> = remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(swipeAreaColor.value ?: MaterialTheme.colorScheme.primaryContainer)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.25f) },
                orientation = Orientation.Horizontal
            ),
        contentAlignment = Alignment.Center
    ) {
        if (swipeOffset >= 0) {
            swipeAreaColor.value = MaterialTheme.colorScheme.primaryContainer
        } else {
            swipeAreaColor.value = MaterialTheme.colorScheme.tertiaryContainer
        }

        // Trigger dismissal when swipeable state reaches the threshold
        if (swipeableState.currentValue == 1) {
            onDismiss()
        } else if (swipeableState.currentValue == 2) {
            onComplete(todo.id)
        }

        // Tick icon animation when swiping right
        if (swipeOffset > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp)
                    .size((swipeOffset * 0.1.dp).coerceAtMost(24.dp))
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_check_24),
                    contentDescription = "Tick",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        if (swipeOffset < 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
                    .size((-swipeOffset * 0.1.dp).coerceAtMost(24.dp))
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_calendar_24),
                    contentDescription = "Postpone",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        // Offset the card based on the swipe progress
        Card(
            modifier = Modifier
                .offset { IntOffset(swipeOffset.toInt(), 0) }
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            onClick = {
                navController.navigate("add/${todo.id}")
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .heightIn(min = 64.dp)
                        .background(
                            color = when (getPriorityByValue(todo.priority)) {
                                TaskPriority.High -> HighPriorityColor
                                TaskPriority.Medium -> MediumPriorityColor
                                TaskPriority.Low -> LowPriorityColor
                            }
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todo.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}
