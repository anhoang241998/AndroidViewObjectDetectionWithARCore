package com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.annguyenhoang.core_ui.R
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.FragmentMlkitObjectDetectionBinding
import com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation.model.BoxWithText
import com.annguyenhoang.core_ui.ext.gone
import com.annguyenhoang.core_ui.ext.hide
import com.annguyenhoang.core_ui.ext.isShow
import com.annguyenhoang.core_ui.ext.setOnThrottleClicked
import com.annguyenhoang.core_ui.ext.show
import com.annguyenhoang.core_ui.ext.showToast
import com.annguyenhoang.core_ui.fragment_binding.ViewBindingFragment
import com.annguyenhoang.core_ui.permission.CameraPermissionHandlerImpl
import com.annguyenhoang.core_ui.permission.PermissionHandler
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import timber.log.Timber

class MLKitObjectDetectionFragment : ViewBindingFragment<FragmentMlkitObjectDetectionBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMlkitObjectDetectionBinding
        get() = FragmentMlkitObjectDetectionBinding::inflate

    private val cameraPermissionHandler by inject<PermissionHandler>(
        qualifier = named<CameraPermissionHandlerImpl>()
    )

    private val viewModel: MLKitObjectDetectionViewModel by viewModel()

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Permission granted/rejected
        var allPermissionGranted = true
        permissions.entries.forEach { permission ->
            val (permissionName, permissionStatus) = permission
            if (permissionName in cameraPermissionHandler.requiredCameraPermissions && !permissionStatus) {
                allPermissionGranted = false
            }
        }

        if (!allPermissionGranted) {
            context?.showToast(getString(R.string.permission_request_denied))
        } else {
            startCamera()
        }
    }

    private var isShowingCameraForCapture = false
    private var imageCapture: ImageCapture? = null

    override fun initViews() {
        super.initViews()
        observeTakenPhoto()
    }

    override fun initControls() {
        super.initControls()

        binding.imgSampleOne.setOnThrottleClicked {
            setViewAndStartDetect(
                getSampleImageFromDrawable(R.drawable.demo_img1)
            )
        }

        binding.imgSampleTwo.setOnThrottleClicked {
            setViewAndStartDetect(
                getSampleImageFromDrawable(R.drawable.demo_img2)
            )
        }

        binding.imgSampleThree.setOnThrottleClicked {
            setViewAndStartDetect(
                getSampleImageFromDrawable(R.drawable.demo_img3)
            )
        }

        binding.captureImageFab.setOnThrottleClicked {
            if (cameraPermissionHandler.allPermissionsGranted().not()) {
                cameraPermissionHandler.requestPermissions(permissionsLauncher)
            }

            if (!isShowingCameraForCapture) {
                startCamera()
                return@setOnThrottleClicked
            }

            val context = context
            val imageCapture = imageCapture
            if (context == null || imageCapture == null) return@setOnThrottleClicked

            viewModel.takePhoto(
                mainExecutors = ContextCompat.getMainExecutor(requireContext()),
                imageCapture = imageCapture,
                onImageCapturedSuccess = {
                    binding.cameraImagePreview.gone()
                }
            )
        }
    }

    private fun observeTakenPhoto() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photoTaken.collect { curTakenPhotos ->
                    curTakenPhotos.lastOrNull()?.let { lastPhoto ->
                        setViewAndStartDetect(photo = lastPhoto)
                    }
                }
            }
        }
    }

    private fun setViewAndStartDetect(photo: Bitmap) {
        if (binding.cameraImagePreview.isShow()) {
            binding.cameraImagePreview.gone()
        }

        isShowingCameraForCapture = false
        binding.imageView.load(photo)
        binding.tvPlaceholder.hide()

        viewModel.runObjectDetection(photo) { detectedResults ->
            val visualizedResult = drawDetectionResult(photo, detectedResults)
            binding.imageView.load(visualizedResult)
        }
    }

    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<BoxWithText>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        paint.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // Draw bounding box
            paint.color = Color.RED
            paint.strokeWidth = 8f
            paint.style = Paint.Style.STROKE
            val box = it.box
            canvas.drawRect(box, paint)

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.color = Color.YELLOW
            paint.strokeWidth = 2f

            paint.textSize = 96f
            paint.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = paint.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < paint.textSize) {
                paint.textSize = fontSize
            }

            var margin = (box.width() - tagSize.width()) / 2f
            if (margin < 0f) {
                margin = 0f
            }

            canvas.drawText(
                it.text,
                box.left + margin,
                box.top + tagSize.height().times(1f),
                paint
            )
        }

        return outputBitmap
    }

//    private fun drawDetectionResult(
//        bitmap: Bitmap,
//        detectionResults: List<DetectionResult>
//    ): Bitmap {
//        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//        val canvas = Canvas(outputBitmap)
//        val paint = Paint()
//        paint.textAlign = Paint.Align.LEFT
//
//        detectionResults.forEach {
//            // Draw bounding box
//            paint.color = Color.RED
//            paint.strokeWidth = 8f
//            paint.style = Paint.Style.STROKE
//            val box = it.boundingBox
//            canvas.drawRect(box, paint)
//
//            val tagSize = Rect(0, 0, 0, 0)
//
//            // calculate the right font size
//            paint.style = Paint.Style.FILL_AND_STROKE
//            paint.color = Color.YELLOW
//            paint.strokeWidth = 2f
//
//            paint.textSize = 96f
//            paint.getTextBounds(it.text, 0, it.text.length, tagSize)
//            val fontSize: Float = paint.textSize * box.width() / tagSize.width()
//
//            // adjust the font size so texts are inside the bounding box
//            if (fontSize < paint.textSize) {
//                paint.textSize = fontSize
//            }
//
//            var margin = (box.width() - tagSize.width()) / 2f
//            if (margin < 0f) {
//                margin = 0f
//            }
//
//            canvas.drawText(
//                it.text,
//                box.left + margin,
//                box.top + tagSize.height().times(1f),
//                paint
//            )
//        }
//
//        return outputBitmap
//    }

    private fun startCamera() {
        try {
            val context = requireContext()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(
                context
            )

            binding.cameraImagePreview.show()

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.cameraImagePreview.surfaceProvider)
                    }

                // initialize image capture
                imageCapture = ImageCapture.Builder()
                    .build()

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                )
            }, ContextCompat.getMainExecutor(context))

            isShowingCameraForCapture = true
        } catch (e: IllegalStateException) {
            Timber.e("There are no context attach to this fragment!")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun getSampleImageFromDrawable(@DrawableRes drawableId: Int): Bitmap {
        return BitmapFactory.decodeResource(
            resources,
            drawableId,
            BitmapFactory.Options().apply {
                inMutable = true
            }
        )
    }

}