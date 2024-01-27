package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.alligatorvscrocodileclassifier.core.utils.sendIn
import com.android.maxclub.alligatorvscrocodileclassifier.core.utils.update
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.AlligatorCrocodileClassifier
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationException
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

    private val uiActionChannel = Channel<ClassificationUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    fun openCamera() {
        _uiState.update { ClassificationUiState.NoImage }
    }

    fun openGallery() {
        loadImage("https://upload.wikimedia.org/wikipedia/commons/2/29/Pangil_Crocodile_Park_Davao_City.jpg")
    }

    fun loadImage(imageUrl: String) {
        _uiState.update { ClassificationUiState.ImageSelected.Loading(imageUrl) }
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