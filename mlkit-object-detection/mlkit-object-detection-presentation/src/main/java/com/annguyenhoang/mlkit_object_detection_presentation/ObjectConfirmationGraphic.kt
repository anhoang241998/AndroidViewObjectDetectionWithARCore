package com.annguyenhoang.mlkit_object_detection_presentation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.annguyenhoang.core_ui.camera.Graphic
import com.annguyenhoang.core_ui.camera.GraphicOverlay
import com.annguyenhoang.core_ui.utils.PreferenceUtils
import com.annguyenhoang.mlkit_object_detection_presentation.objectdetection.ObjectConfirmationController

/**
 * Similar to the camera reticle but with additional progress ring to indicate an object is getting
 * confirmed for a follow up processing, e.g. product search.
 */
class ObjectConfirmationGraphic internal constructor(
    overlay: GraphicOverlay,
    private val confirmationController: ObjectConfirmationController
) : Graphic(overlay) {

    private val outerRingFillPaint: Paint
    private val outerRingStrokePaint: Paint
    private val innerRingPaint: Paint
    private val progressRingStrokePaint: Paint
    private val outerRingFillRadius: Int
    private val outerRingStrokeRadius: Int
    private val innerRingStrokeRadius: Int

    init {

        val resources = overlay.resources
        outerRingFillPaint = Paint().apply {
            style = Style.FILL
            color = ContextCompat.getColor(context, com.annguyenhoang.core_ui.R.color.object_reticle_outer_ring_fill)
        }

        outerRingStrokePaint = Paint().apply {
            style = Style.STROKE
            strokeWidth =
                resources.getDimensionPixelOffset(com.annguyenhoang.core_ui.R.dimen.object_reticle_outer_ring_stroke_width)
                    .toFloat()
            strokeCap = Cap.ROUND
            color = ContextCompat.getColor(context, com.annguyenhoang.core_ui.R.color.object_reticle_outer_ring_stroke)
        }

        progressRingStrokePaint = Paint().apply {
            style = Style.STROKE
            strokeWidth =
                resources.getDimensionPixelOffset(com.annguyenhoang.core_ui.R.dimen.object_reticle_outer_ring_stroke_width)
                    .toFloat()
            strokeCap = Cap.ROUND
            color = ContextCompat.getColor(context, com.annguyenhoang.core_ui.R.color.white)
        }

        innerRingPaint = Paint()
        if (PreferenceUtils.isMultipleObjectsMode(overlay.context)) {
            innerRingPaint.style = Style.FILL
            innerRingPaint.color =
                ContextCompat.getColor(context, com.annguyenhoang.core_ui.R.color.object_reticle_inner_ring)
        } else {
            innerRingPaint.style = Style.STROKE
            innerRingPaint.strokeWidth =
                resources.getDimensionPixelOffset(com.annguyenhoang.core_ui.R.dimen.object_reticle_inner_ring_stroke_width)
                    .toFloat()
            innerRingPaint.strokeCap = Cap.ROUND
            innerRingPaint.color = ContextCompat.getColor(context, com.annguyenhoang.core_ui.R.color.white)
        }

        outerRingFillRadius =
            resources.getDimensionPixelOffset(com.annguyenhoang.core_ui.R.dimen.object_reticle_outer_ring_fill_radius)
        outerRingStrokeRadius =
            resources.getDimensionPixelOffset(com.annguyenhoang.core_ui.R.dimen.object_reticle_outer_ring_stroke_radius)
        innerRingStrokeRadius =
            resources.getDimensionPixelOffset(com.annguyenhoang.core_ui.R.dimen.object_reticle_inner_ring_stroke_radius)
    }

    override fun draw(canvas: Canvas) {
        val cx = canvas.width / 2f
        val cy = canvas.height / 2f
        canvas.drawCircle(cx, cy, outerRingFillRadius.toFloat(), outerRingFillPaint)
        canvas.drawCircle(cx, cy, outerRingStrokeRadius.toFloat(), outerRingStrokePaint)
        canvas.drawCircle(cx, cy, innerRingStrokeRadius.toFloat(), innerRingPaint)

        val progressRect = RectF(
            cx - outerRingStrokeRadius,
            cy - outerRingStrokeRadius,
            cx + outerRingStrokeRadius,
            cy + outerRingStrokeRadius
        )
        val sweepAngle = confirmationController.progress * 360
        canvas.drawArc(
            progressRect,
            /* startAngle= */ 0f,
            sweepAngle,
            /* useCenter= */ false,
            progressRingStrokePaint
        )
    }
}