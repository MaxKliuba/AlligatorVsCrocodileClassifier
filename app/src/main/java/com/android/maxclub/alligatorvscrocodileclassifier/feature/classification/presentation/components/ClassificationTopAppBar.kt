package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.maxclub.alligatorvscrocodileclassifier.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassificationTopAppBar(
    onClickInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.classification_title))
        },
        actions = {
            IconButton(onClick = onClickInfo) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.info_button),
                )
            }
        },
        modifier = modifier
    )
}