package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.alligatorvscrocodileclassifier.R
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationErrorComponent
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationResultComponent
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationResultLoadingComponent
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationTopAppBar
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.EmptyScreenPlaceholder
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectImageFab
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectedImage
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectedImageLoadingComponent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ClassificationScreen(viewModel: ClassificationViewModel = hiltViewModel()) {
    val state by viewModel.uiState

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ClassificationUiAction.ShowImageLoadingErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.image_loading_error_message),
                        actionLabel = context.getString(R.string.retry_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.loadImage(action.imageUrl)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ClassificationTopAppBar(onClickInfo = { /*TODO*/ })
        },
        floatingActionButton = {
            SelectImageFab(
                onOpenCamera = viewModel::openCamera,
                onOpenGallery = viewModel::openGallery,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            state.let { state ->
                when (state) {
                    is ClassificationUiState.NoImage -> {
                        EmptyScreenPlaceholder(modifier = Modifier.align(Alignment.Center))
                    }

                    is ClassificationUiState.ImageSelected -> {
                        if (state is ClassificationUiState.ImageSelected.Loading) {
                            SelectedImageLoadingComponent(modifier = Modifier.align(Alignment.Center))
                        }

                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                Row {
                                    SelectedImage(
                                        imageUrl = state.imageUrl,
                                        onSuccess = viewModel::onImageLoaded,
                                        onError = viewModel::onImageLoadingError,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(
                                                start = 16.dp,
                                                top = 16.dp,
                                                end = 8.dp,
                                                bottom = 16.dp
                                            )
                                    )

                                    val classificationResultModifier = Modifier.padding(
                                        start = 8.dp,
                                        top = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )

                                    when (state) {
                                        is ClassificationUiState.ImageSelected.Classifying -> {
                                            ClassificationResultLoadingComponent(
                                                modifier = classificationResultModifier
                                            )
                                        }

                                        is ClassificationUiState.ImageSelected.ClassificationSuccess -> {
                                            ClassificationResultComponent(
                                                result = state.result,
                                                onReadForDetails = { /*TODO*/ },
                                                modifier = classificationResultModifier
                                            )
                                        }

                                        is ClassificationUiState.ImageSelected.ClassificationError -> {
                                            ClassificationErrorComponent(
                                                onRetry = {
                                                    viewModel.classifyImage(
                                                        state.imageUrl,
                                                        state.image
                                                    )
                                                },
                                                modifier = classificationResultModifier
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                            }

                            else -> {
                                Column {
                                    SelectedImage(
                                        imageUrl = state.imageUrl,
                                        onSuccess = viewModel::onImageLoaded,
                                        onError = viewModel::onImageLoadingError,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )

                                    val classificationResultModifier =
                                        Modifier.padding(horizontal = 16.dp)

                                    when (state) {
                                        is ClassificationUiState.ImageSelected.Classifying -> {
                                            ClassificationResultLoadingComponent(
                                                modifier = classificationResultModifier
                                            )
                                        }

                                        is ClassificationUiState.ImageSelected.ClassificationSuccess -> {
                                            ClassificationResultComponent(
                                                result = state.result,
                                                onReadForDetails = { /*TODO*/ },
                                                modifier = classificationResultModifier
                                            )
                                        }

                                        is ClassificationUiState.ImageSelected.ClassificationError -> {
                                            ClassificationErrorComponent(
                                                onRetry = {
                                                    viewModel.classifyImage(
                                                        state.imageUrl,
                                                        state.image
                                                    )
                                                },
                                                modifier = classificationResultModifier
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}