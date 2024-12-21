package com.mikeapp.newideatodoapp.main.add.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabelFlowRow() {
    FlowRow(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(10) { _ ->
            LabeledBox("Home", ImageVector.vectorResource(id = R.drawable.baseline_location_pin_24))
        }
    }
}