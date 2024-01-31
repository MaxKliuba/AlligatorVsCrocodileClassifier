package com.tech.maxclub.alligatorvscrocodileclassifier.feature.main.presentation

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.ClassificationScreen
import com.tech.maxclub.alligatorvscrocodileclassifier.ui.theme.AlligatorVsCrocodileClassifierTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlligatorVsCrocodileClassifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val browserScreen = buildBrowserScreen()

                    ClassificationScreen(
                        onNavigateToScreenByUrl = { url ->
                            browserScreen.launchUrl(this@MainActivity, Uri.parse(url))
                        }
                    )
                }
            }
        }
    }
}