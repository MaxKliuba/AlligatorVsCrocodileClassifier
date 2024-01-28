package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.alligatorvscrocodileclassifier.BuildConfig
import com.tech.maxclub.alligatorvscrocodileclassifier.core.utils.createImageFile
import com.tech.maxclub.alligatorvscrocodileclassifier.core.utils.sendIn
import com.tech.maxclub.alligatorvscrocodileclassifier.core.utils.update
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.AlligatorCrocodileClassifier
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassificationViewModel @Inject constructor(
    private val alligatorCrocodileClassifier: AlligatorCrocodileClassifier,
) : ViewModel() {

    private val _uiState = mutableStateOf<ClassificationUiState>(ClassificationUiState.NoImage)
    val uiState = _uiState

    val isCameraPermissionRationaleDialogVisible = mutableStateOf(false)

    private val uiActionChannel = Channel<ClassificationUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    var capturedImageUri: Uri? = null
        private set

    fun openGallery() {
        uiActionChannel.sendIn(
            ClassificationUiAction.RequestImagePickerLauncher(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            ),
            viewModelScope
        )
    }

    fun openCamera(context: Context) {
        val imageUri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            context.createImageFile()
        )
        capturedImageUri = imageUri
        println(imageUri)

        uiActionChannel.sendIn(
            ClassificationUiAction.RequestCameraLauncher(imageUri),
            viewModelScope
        )
    }

    fun showCameraPermissionRationaleDialog() {
        isCameraPermissionRationaleDialogVisible.update { true }
    }

    fun dismissCameraPermissionRationaleDialog() {
        isCameraPermissionRationaleDialogVisible.update { false }
    }

    fun loadImageByUrl(imageUrl: String) {
        if ((_uiState.value as? ClassificationUiState.ImageSelected)?.imageUrl != imageUrl) {
            _uiState.update { ClassificationUiState.ImageSelected.Loading(imageUrl) }
        }
    }

    fun loadCapturedImage() {
        capturedImageUri?.toString()?.let { imageUrl ->
            loadImageByUrl(imageUrl)
        }
    }

    fun onImageLoaded(imageUrl: String, image: Drawable) {
        if (_uiState.value is ClassificationUiState.ImageSelected.Loading) {
            classifyImage(imageUrl, image)
        }
    }

    fun onImageLoadingError(imageUrl: String) {
        _uiState.update { ClassificationUiState.NoImage }

        uiActionChannel.sendIn(
            ClassificationUiAction.ShowImageLoadingErrorMessage(imageUrl),
            viewModelScope
        )
    }

    fun classifyImage(imageUrl: String, image: Drawable) {
        _uiState.update { ClassificationUiState.ImageSelected.Classifying(imageUrl) }

        viewModelScope.launch {
            val newUiState = try {
                ClassificationUiState.ImageSelected.ClassificationSuccess(
                    imageUrl = imageUrl,
                    result = alligatorCrocodileClassifier.classify(
                        alligatorCrocodileClassifier.convertToBitmap(image)
                    ),
                )
            } catch (e: ClassificationException) {
                e.printStackTrace()

                ClassificationUiState.ImageSelected.ClassificationError(imageUrl, image)
            }

            _uiState.update { newUiState }
        }
    }
}