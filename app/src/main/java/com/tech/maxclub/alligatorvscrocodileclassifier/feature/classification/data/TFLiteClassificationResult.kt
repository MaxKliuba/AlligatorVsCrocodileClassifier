package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.data

import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationResult

enum class TFLiteClassificationResult {
    ALLIGATOR,
    CROCODILE,
}

fun TFLiteClassificationResult.toClassificationResult(score: Float): ClassificationResult =
    when (this) {
        TFLiteClassificationResult.ALLIGATOR -> ClassificationResult.Alligator(score)
        TFLiteClassificationResult.CROCODILE -> ClassificationResult.Crocodile(score)
    }