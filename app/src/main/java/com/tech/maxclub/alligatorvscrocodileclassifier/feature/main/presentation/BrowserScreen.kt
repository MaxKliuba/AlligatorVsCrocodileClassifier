package com.tech.maxclub.alligatorvscrocodileclassifier.feature.main.presentation

import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb

@Composable
fun buildBrowserScreen(): CustomTabsIntent =
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(MaterialTheme.colorScheme.secondaryContainer.toArgb())
                .build()
        )
        .build()