package com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation.model

import android.graphics.Rect

data class BoxWithText(
    val box: Rect,
    val text: String
)
