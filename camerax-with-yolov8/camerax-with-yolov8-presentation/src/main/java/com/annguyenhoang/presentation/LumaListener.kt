package com.annguyenhoang.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

typealias LumaListener = (luma: Double) -> Unit

class LuminosityAnalyzer : ImageAnalysis.Analyzer {

    private var listener: LumaListener? = null

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = buffer.toByteArray()
        val pixels = data.map { it.toInt() and 0xFF }
        val luma = pixels.average()
        listener?.invoke(luma)
        image.close()
    }

    fun setOnLumaListener(listener: LumaListener) {
        this.listener = listener
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data) // Copy the buffer into a byte array
        return data
    }
}