package com.android.maxclub.alligatorvscrocodileclassifier.feature.main.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.ClassificationScreen
import com.android.maxclub.alligatorvscrocodileclassifier.ui.theme.AlligatorVsCrocodileClassifierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlligatorVsCrocodileClassifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClassificationScreen()
                }
            }
        }
    }
}