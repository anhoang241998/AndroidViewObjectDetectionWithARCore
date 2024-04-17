package com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation.model.BoxWithText
import com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation.model.DetectionResult
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import timber.log.Timber
import java.util.concurrent.Executor

class MLKitObjectDetectionViewModel(
    private val application: Application
) : ViewModel() {

    private val _photoTaken = MutableStateFlow<List<Bitmap>>(emptyList())
    val photoTaken: StateFlow<List<Bitmap>>
        get() = _photoTaken.asStateFlow()

    fun takePhoto(
        mainExecutors: Executor,
        imageCapture: ImageCapture,
        onImageCapturedSuccess: (Bitmap) -> Unit
    ) {
        try {
            imageCapture.takePicture(
                mainExecutors,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)

                        val matrix = Matrix().apply {
                            postRotate(image.imageInfo.rotationDegrees.toFloat())
                        }
                        val rotatedBitmap = Bitmap.createBitmap(
                            image.toBitmap(),
                            0,
                            0,
                            image.width,
                            image.height,
                            matrix,
                            true
                        )

                        _photoTaken.update {
                            val curTakenPhoto = it.toMutableList()
                            curTakenPhoto += rotatedBitmap

                            curTakenPhoto.toList()
                        }

                        onImageCapturedSuccess(rotatedBitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        Timber.e("Photo capture failed: ${exception.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun runObjectDetection(
        photo: Bitmap,
        onObjectsDetected: (detectedResults: List<BoxWithText>) -> Unit
    ) {
        val customPreTrainedModel = LocalModel.Builder()
            .setAssetFilePath("test_object_detection.tflite")
            .build()

        // 5 image classification categories:

        val inputImage = InputImage.fromBitmap(photo, 0)
//        val inputImage = TensorImage.fromBitmap(photo)
        val options = CustomObjectDetectorOptions.Builder(customPreTrainedModel)
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .setClassificationConfidenceThreshold(.25f)
            .setMaxPerObjectLabelCount(3)
            .build()

//        val options = ObjectDetector.ObjectDetectorOptions.builder()
//            .setMaxResults(5)
//            .setScoreThreshold(.5f)
//            .build()

//        val objectDetector = ObjectDetector.createFromFileAndOptions(
//            application.applicationContext,
//            "mobile_object_labeler.tflite",
//            options
//        )

        val objectDetector = ObjectDetection.getClient(options)
//        val objectDetector = ObjectDetector.createFromFileAndOptions(
//            application.applicationContext,
//            "mobile_object_labeler.tflite",
//            options
//        )

        viewModelScope.launch(Dispatchers.Default) {
//            val detectedObjects = objectDetector.detect(inputImage)
//            val detectedResults = detectedObjects.map { obj ->
//                // Get the top-1 category and craft the display text
//                val category = obj.categories.first()
//                val text = "${category.label}, ${category.score.times(100).toInt()}%"
//
//                DetectionResult(
//                    obj.boundingBox,
//                    text
//                )
//            }
//
//            withContext(Dispatchers.Main) {
//                onObjectsDetected(detectedResults)
//            }
            objectDetector.process(inputImage)
                .addOnSuccessListener { detectedObjects ->
                    detectedObjects.forEachIndexed { index, detectedObject ->
                        debugPrint(index, detectedObject)
                    }

                    val detectedResults = detectedObjects.map { obj ->
                        var text = "Unknown"

                        // We will show the top confident detection result if it exist
                        if (obj.labels.isNotEmpty()) {
                            val firstLabel = obj.labels.first()
                            text = "${firstLabel.text}, ${firstLabel.confidence.times(100).toInt()}"
                        }

                        BoxWithText(
                            box = obj.boundingBox,
                            text = text
                        )
                    }

                    viewModelScope.launch(Dispatchers.Main) {
                        onObjectsDetected(detectedResults)
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception.message.toString())
                }
        }
    }

    private fun debugPrint(index: Int, detectedObject: DetectedObject) {
        val box = detectedObject.boundingBox
        Timber.tag("ANDEBUG").d("Detected object: $index")
        Timber.tag("ANDEBUG").d("trackingId: ${detectedObject.trackingId}")
        Timber.tag("ANDEBUG").d("boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")
        detectedObject.labels.forEach {
            Timber.tag("ANDEBUG").d("categories: ${it.text}")
            Timber.tag("ANDEBUG").d("confidence: ${it.confidence}")
        }
    }
}