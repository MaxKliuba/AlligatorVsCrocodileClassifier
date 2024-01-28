package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.tech.maxclub.alligatorvscrocodileclassifier.R
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.CameraPermissionRationaleDialog
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationErrorComponent
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationResultComponent
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationResultLoadingComponent
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.ClassificationTopAppBar
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.EmptyScreenPlaceholder
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectImageFab
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectedImage
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation.components.SelectedImageLoadingComponent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ClassificationScreen(viewModel: ClassificationViewModel = hiltViewModel()) {
    val state by viewModel.uiState
    val isCameraPermissionRationaleDialogVisible by viewModel.isCameraPermissionRationaleDialogVisible
    var parentSize by remember { mutableStateOf(IntSize.Zero) }

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val imagePickerResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let { viewModel.loadImageByUrl(imageUri.toString()) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.loadCapturedImage()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.capturedImageUri?.let { imageUri ->
                cameraLauncher.launch(imageUri)
            }
        }
    }

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
                            viewModel.loadImageByUrl(action.imageUrl)
                        }
                    }
                }

                is ClassificationUiAction.RequestImagePickerLauncher -> {
                    imagePickerResultLauncher.launch(action.imagePickerRequest)
                }

                is ClassificationUiAction.RequestCameraLauncher -> {
                    val isCameraAvailable =
                        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                    val cameraPermission = Manifest.permission.CAMERA

                    when {
                        !isCameraAvailable -> {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.camera_is_unavailable_message),
                                withDismissAction = true,
                                duration = SnackbarDuration.Short,
                            )
                        }

                        ContextCompat.checkSelfPermission(
                            context,
                            cameraPermission
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            cameraLauncher.launch(action.imageUri)
                        }

                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            cameraPermission
                        ) -> {
                            viewModel.showCameraPermissionRationaleDialog()
                        }

                        else -> {
                            cameraPermissionLauncher.launch(cameraPermission)
                        }
                    }
                }
            }
        }
    }

    if (isCameraPermissionRationaleDialogVisible) {
        CameraPermissionRationaleDialog(onDismiss = viewModel::dismissCameraPermissionRationaleDialog)
    }

    Scaffold(
        topBar = {
            ClassificationTopAppBar(onClickInfo = { /*TODO*/ })
        },
        floatingActionButton = {
            SelectImageFab(
                onOpenGallery = viewModel::openGallery,
                onOpenCamera = { viewModel.openCamera(context) },
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
                .onSizeChanged { parentSize = it }
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
                                        contentScale = ContentScale.FillHeight,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .widthIn(max = with(density) { parentSize.width.toDp() * 0.65f })
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
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = with(density) { parentSize.height.toDp() * 0.65f })
                                            .padding(16.dp)
                                    )

                                    val classificationResultModifier =
                                        Modifier.padding(
                                            start = 16.dp,
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
                        }
                    }
                }
            }
        }
    }
}