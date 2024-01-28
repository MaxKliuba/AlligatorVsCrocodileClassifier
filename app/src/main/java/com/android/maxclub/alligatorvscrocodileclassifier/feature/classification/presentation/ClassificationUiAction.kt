package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest

sealed class ClassificationUiAction {
    data class ShowImageLoadingErrorMessage(val imageUrl: String) : ClassificationUiAction()
    data class RequestImagePickerLauncher(val imagePickerRequest: PickVisualMediaRequest) :
        ClassificationUiAction()

    data class RequestCameraLauncher(val imageUri: Uri) : ClassificationUiAction()
}