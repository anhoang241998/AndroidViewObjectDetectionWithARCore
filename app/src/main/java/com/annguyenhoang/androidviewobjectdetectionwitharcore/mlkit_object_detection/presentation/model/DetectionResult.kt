package com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation.model

import android.graphics.RectF

data class DetectionResult(
    val boundingBox: RectF,
    val text: String
)