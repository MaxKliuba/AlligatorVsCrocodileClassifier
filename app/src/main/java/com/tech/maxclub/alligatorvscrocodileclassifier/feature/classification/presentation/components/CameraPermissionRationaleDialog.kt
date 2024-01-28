package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.tech.maxclub.alligatorvscrocodileclassifier.R

@Composable
fun CameraPermissionRationaleDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.camera_permission_dialog_title),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(text = stringResource(id = R.string.camera_permission_dialog_text))
        },
        icon = {
            Icon(
                imageVector = Icons.Default.PermDeviceInformation,
                contentDescription = null
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    launchPermissionSettingsIntent(context)
                }
            ) {
                Text(text = stringResource(R.string.allow_in_settings_button))
            }
        },
        modifier = modifier,
    )
}

fun launchPermissionSettingsIntent(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}