package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeField(dateTime: LocalDateTime, isNotificationOn: Boolean, onSelected: () -> Unit) {
    val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a"))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelected.invoke()
            }
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_calendar_today_24),
            contentDescription = null
        )
        Text(
            text = formattedDateTime,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
        val icon =
            if (isNotificationOn) R.drawable.baseline_notifications_active_24 else R.drawable.baseline_notifications_off_24
        val tint =
            if (isNotificationOn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}