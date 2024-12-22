package com.mikeapp.newideatodoapp.main.add.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikeapp.newideatodoapp.data.enums.getPriorityByValue
import com.mikeapp.newideatodoapp.main.add.model.LocationUi
import java.time.LocalDateTime

@Composable
fun AttributeList(
    priority: Int? = null,
    location: LocationUi? = null,
    dateTime: LocalDateTime? = null,
    isLocationNotificationOn: Boolean,
    isDateTimeNotificationOn: Boolean,
    modifier: Modifier = Modifier,
    onLocationClicked: () -> Unit,
    onDateTimeClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (priority != null) {
            PriorityField(getPriorityByValue(priority)) { }
            HorizontalDivider()
        }

        if (location != null) {
            LocationField(location, isLocationNotificationOn) {
                onLocationClicked()
            }
            HorizontalDivider()
        }

        if (dateTime != null) {
            DateTimeField(dateTime, isDateTimeNotificationOn) {
                onDateTimeClicked()
            }
            HorizontalDivider()
        }
    }
}