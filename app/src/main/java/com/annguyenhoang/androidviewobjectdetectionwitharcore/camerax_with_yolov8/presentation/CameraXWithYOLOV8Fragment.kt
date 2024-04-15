package com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.presentation

import android.Manifest
import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.annguyenhoang.androidviewobjectdetectionwitharcore.R
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.Constants.FILENAME_FORMAT
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.ext.setOnThrottleClicked
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.ext.showToast
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.fragment_binding.ViewBindingFragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.permission.CameraPermissionHandlerImpl
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.permission.PermissionHandler
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.FragmentCameraxWithYolov8Binding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun initViews() {
        super.initViews()
        cameraExecutor = Executors.newSingleThreadExecutor()
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
            takePhoto()
        }

        binding.videoCaptureButton.setOnThrottleClicked {
            captureVideo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
//
//                val luminosityAnalyzer = LuminosityAnalyzer()
//                val imageAnalyzer = ImageAnalysis.Builder()
//                    .build()
//                    .also {
//                        it.setAnalyzer(
//                            cameraExecutor,
//                            luminosityAnalyzer
//                        )
//                    }
//
//                luminosityAnalyzer.setOnLumaListener { luma ->
//                    Timber.tag("ANDEBUG").d("Average luminosity: $luma")
//                }

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
//                    imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(context))
        } catch (e: IllegalStateException) {
            Timber.e("There are no context attach to this fragment!")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun takePhoto() {
        try {
            // Get a stable reference of the modifiable image capture use case
            val imageCapture = this.imageCapture ?: return
            val contentResolver = activity?.contentResolver ?: return
            val context = context ?: return

            // Create time stamped name and MediaStore entry.
            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
                }
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(
                    contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()

            // Set up image capture listener, which is triggered after photo has
            // been taken
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val msg = "Photo capture succeeded: ${outputFileResults.savedUri}"
                        context.showToast(msg)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Timber.e("Photo capture failed: ${exception.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun captureVideo() {
        try {
            val videoCapture = this.videoCapture ?: return
            val contentResolver = activity?.contentResolver ?: return
            val context = context ?: return

            binding.videoCaptureButton.isEnabled = false

            val curRecording = recording
            if (curRecording != null) {
                // Stop the current recording session
                curRecording.stop()
                recording = null
                return
            }

            // create and start a new recording session
            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/CameraX-Video")
                }
            }

            val mediaStoreOutputOptions = MediaStoreOutputOptions
                .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues)
                .build()

            recording = videoCapture.output
                .prepareRecording(context, mediaStoreOutputOptions)
                .apply {
                    if (PermissionChecker.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PermissionChecker.PERMISSION_GRANTED
                    ) {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            binding.videoCaptureButton.apply {
                                text = this@CameraXWithYOLOV8Fragment.getString(R.string.stop_capture)
                                isEnabled = true
                            }
                        }

                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                                context.showToast(msg)
                            } else {
                                recording?.close()
                                recording = null
                                Timber.tag("ANDEBUG").e("Video capture ends with error: ${recordEvent.error}")
                            }

                            binding.videoCaptureButton.apply {
                                text = this@CameraXWithYOLOV8Fragment.getString(R.string.start_capture)
                                isEnabled = true
                            }
                        }
                    }

                }
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