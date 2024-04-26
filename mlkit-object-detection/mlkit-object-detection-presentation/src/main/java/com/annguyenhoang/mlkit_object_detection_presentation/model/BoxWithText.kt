package com.annguyenhoang.mlkit_object_detection_presentation.model

import android.graphics.Rect

data class BoxWithText(
    val box: Rect,
    val text: String
)
