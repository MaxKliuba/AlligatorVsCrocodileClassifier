package com.android.maxclub.alligatorvscrocodileclassifier.core.utils

import android.content.Context
import java.io.File
import java.util.Date

fun Context.createImageFile(): File =
    File.createTempFile(
        "IMG_${Date().time}",
        ".jpg",
        externalCacheDir,
    )