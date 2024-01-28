package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

import android.graphics.drawable.Drawable
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationResult

sealed class ClassificationUiState {
    data object NoImage : ClassificationUiState()
    sealed class ImageSelected(val imageUrl: String) : ClassificationUiState() {
        class Loading(imageUrl: String) : ImageSelected(imageUrl)
        class Classifying(imageUrl: String) : ImageSelected(imageUrl)
        class ClassificationSuccess(imageUrl: String, val result: ClassificationResult) :
            ImageSelected(imageUrl)

        class ClassificationError(imageUrl: String, val image: Drawable) : ImageSelected(imageUrl)
    }
}