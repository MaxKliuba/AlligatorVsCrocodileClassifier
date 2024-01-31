package com.tech.maxclub.alligatorvscrocodileclassifier.core.utils

import android.content.Context
import java.io.File
import java.util.Date

fun Context.clearExternalCacheDir(): Boolean =
    externalCacheDir?.deleteRecursively() ?: false

fun Context.createImageFile(): File =
    File.createTempFile(
        "IMG_${Date().time}",
        ".jpg",
        externalCacheDir,
    )