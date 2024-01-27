package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

interface AlligatorCrocodileClassifier {

    @Throws(ClassificationException::class)
    suspend fun classify(bitmap: Bitmap): ClassificationResult

    fun convertToBitmap(drawable: Drawable): Bitmap
}