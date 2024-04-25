package com.annguyenhoang.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.annguyenhoang.core_ui.R
import com.annguyenhoang.camerax_with_yolov8_presentation.databinding.FragmentCameraxWithYolov8Binding
import com.annguyenhoang.core_ui.ext.setOnThrottleClicked
import com.annguyenhoang.core_ui.ext.showToast
import com.annguyenhoang.core_ui.fragment_binding.ViewBindingFragment
import com.annguyenhoang.core_ui.permission.CameraPermissionHandlerImpl
import com.annguyenhoang.core_ui.permission.PermissionHandler
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import timber.log.Timber

class CameraXWithYOLOV8Fragment : ViewBindingFragment<FragmentCameraxWithYolov8Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCameraxWithYolov8Binding
        get() = FragmentCameraxWithYolov8Binding::inflate

    private val viewModel: CameraXWithYOLOV8ViewModel by viewModel()

    private val cameraPermissionHandler by inject<PermissionHandler>(
        qualifier = named<CameraPermissionHandlerImpl>()
    )

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

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    override fun initViews() {
        super.initViews()
        changeStatusBarColor()
        observeApplicationFPS()

        if (cameraPermissionHandler.allPermissionsGranted()) {
            startCamera()
        } else {
            cameraPermissionHandler.requestPermissions(permissionsLauncher)
        }
    }

    override fun initControls() {
        super.initControls()

        binding.imageCaptureButton.setOnThrottleClicked {
            val context = context
            val imageCapture = imageCapture
            if (context == null || imageCapture == null) return@setOnThrottleClicked

            viewModel.takePhotoAndSaveToExternal(
                mainExecutors = ContextCompat.getMainExecutor(context),
                imageCapture = imageCapture,
                onImageSaveSuccess = { msg ->
                    context.showToast(msg)
                }
            )
        }

        binding.videoCaptureButton.setOnThrottleClicked {
            val context = context
            val videoCapture = videoCapture
            if (context == null || videoCapture == null) return@setOnThrottleClicked

            viewModel.captureVideoAndSaveToExternal(
                videoCapture = videoCapture,
                mainExecutors = ContextCompat.getMainExecutor(context),
                onEnableCaptureBtn = { enabled, btnTitle ->
                    binding.videoCaptureButton.apply {
                        if (btnTitle != null) {
                            text = btnTitle
                        }
                        isEnabled = enabled
                    }
                },
            )
        }
    }

    private fun startCamera() {
        try {
            val context = requireContext()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(
                context
            )

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                    }

                val recorder = Recorder.Builder()
                    .setQualitySelector(
                        QualitySelector.from(
                            Quality.HIGHEST,
                            FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                        )
                    )
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)
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
                    videoCapture,
                    imageCapture,
                )
            }, ContextCompat.getMainExecutor(context))
        } catch (e: IllegalStateException) {
            Timber.e("There are no context attach to this fragment!")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun observeApplicationFPS() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeApplicationFPSUseCase.collect { fps ->
                    binding.tvFpsValue.text = "$fps"
                }
            }
        }
    }

    private fun changeStatusBarColor() {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
    }

}