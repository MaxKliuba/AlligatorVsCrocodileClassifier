package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.tech.maxclub.alligatorvscrocodileclassifier.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassificationTopAppBar(
    onClickLogo: () -> Unit,
    onClickInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClickLogo) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = null,
                )
            }
        },
        title = {
            Text(text = stringResource(id = R.string.classification_title))
        },
        actions = {
            IconButton(onClick = { onClickInfo(context.getString(R.string.info_details)) }) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.info_button),
                )
            }
        },
        modifier = modifier
    )
}