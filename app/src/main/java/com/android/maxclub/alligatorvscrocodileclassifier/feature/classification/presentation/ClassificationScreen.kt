package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationTopAppBar
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.EmptyScreenPlaceholder
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectedImageLoadingIndicator
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ResultSection
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectImageFab
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectedImage

@Composable
fun ClassificationScreen() {
    val configuration = LocalConfiguration.current

    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            ClassificationTopAppBar(onClickInfo = { /*TODO*/ })
        },
        floatingActionButton = {
            SelectImageFab(
                onOpenCamera = { selectedImageUrl = null },
                onOpenGallery = {
                    selectedImageUrl =
                        "https://c02.purpledshub.com/uploads/sites/62/2014/11/GettyImages-123529247-2a29d6c.jpg"
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            selectedImageUrl?.let { selectedImageUrl ->
                SelectedImageLoadingIndicator(modifier = Modifier.align(Alignment.Center))

                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        Row {
                            SelectedImage(
                                imageUrl = selectedImageUrl,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 16.dp)
                            )
                            ResultSection(
                                result = .78f,
                                onClickReadForDetails = { /*TODO*/ },
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                            )
                        }
                    }

                    else -> {
                        Column {
                            SelectedImage(
                                imageUrl = selectedImageUrl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                            ResultSection(
                                result = .78f,
                                onClickReadForDetails = { /*TODO*/ },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } ?: EmptyScreenPlaceholder(modifier = Modifier.align(Alignment.Center))
        }
    }
}