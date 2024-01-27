package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.presentation

sealed class ClassificationUiAction {
    data class ShowImageLoadingErrorMessage(val imageUrl: String) : ClassificationUiAction()
}