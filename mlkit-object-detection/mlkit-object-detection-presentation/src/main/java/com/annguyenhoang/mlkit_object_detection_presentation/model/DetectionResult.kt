package com.annguyenhoang.mlkit_object_detection_presentation.model

import android.graphics.RectF

data class DetectionResult(
    val boundingBox: RectF,
    val text: String
)