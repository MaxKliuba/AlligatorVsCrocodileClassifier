package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun SelectedImageLoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        strokeWidth = 6.dp,
        strokeCap = StrokeCap.Round,
        modifier = modifier.size(64.dp)
    )
}