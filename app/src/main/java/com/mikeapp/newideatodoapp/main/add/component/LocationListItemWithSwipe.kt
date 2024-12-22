package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.main.add.model.LocationUi

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LocationListItemWithSwipe(location: LocationUi, onSelected: (LocationUi) -> Unit, onDelete: (LocationUi) -> Unit) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 200.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)
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
        if (swipeOffset <= 0) {
            swipeAreaColor.value = MaterialTheme.colorScheme.tertiaryContainer
        }

        if (swipeableState.currentValue == 1) {
            onDelete(location)
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
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_forever_24),
                    contentDescription = "Postpone",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset { IntOffset(swipeOffset.toInt(), 0) }
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable {
                    onSelected(location)
                }
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_location_pin_24),
                contentDescription = "Location icon"
            )
            Text(
                text = "${location.name} (radius: ${location.radius})",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}