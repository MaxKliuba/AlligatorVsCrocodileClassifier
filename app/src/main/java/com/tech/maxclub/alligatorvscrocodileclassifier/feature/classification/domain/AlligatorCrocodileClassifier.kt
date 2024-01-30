package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain

import android.graphics.Bitmap

interface AlligatorCrocodileClassifier {

    @Throws(ClassificationException::class)
    suspend fun classify(bitmap: Bitmap): ClassificationResult
}