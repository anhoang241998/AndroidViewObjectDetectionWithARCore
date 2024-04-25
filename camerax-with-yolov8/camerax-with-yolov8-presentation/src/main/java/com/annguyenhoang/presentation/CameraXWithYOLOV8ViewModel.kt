package com.annguyenhoang.presentation

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annguyenhoang.camerax_with_yolov8_domain.use_case.ObserveApplicationFPSUseCase
import com.annguyenhoang.core.utils.Constants.FILENAME_FORMAT
import com.annguyenhoang.core.utils.Constants.IMAGE_FOLDER_PATH
import com.annguyenhoang.core.utils.Constants.JPEG_MIME_TYPE
import com.annguyenhoang.core.utils.Constants.MP4_MIME_TYPE
import com.annguyenhoang.core.utils.Constants.VIDEO_FOLDER_PATH
import com.annguyenhoang.core_ui.R
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

class CameraXWithYOLOV8ViewModel(
    private val application: Application,
    observeApplicationFPSUseCase: ObserveApplicationFPSUseCase
) : ViewModel() {

    val observeApplicationFPSUseCase: SharedFlow<Double> = observeApplicationFPSUseCase()
        .map { applicationFPS ->
            applicationFPS.fpsValue
        }
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(300)
        )

    var recording: Recording? = null
        private set

    fun takePhotoAndSaveToExternal(
        mainExecutors: Executor,
        imageCapture: ImageCapture,
        onImageSaveSuccess: ((String) -> Unit)? = null
    ) {
        try {
            val outputOptions = createImageOutputOptions()
            imageCapture.takePicture(
                outputOptions,
                mainExecutors,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val msg = "Photo capture succeeded: ${outputFileResults.savedUri}"
                        onImageSaveSuccess?.invoke(msg)
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

    fun captureVideoAndSaveToExternal(
        videoCapture: VideoCapture<Recorder>,
        mainExecutors: Executor,
        onEnableCaptureBtn: (Boolean, String?) -> Unit,
        onVideoSavedSuccess: ((String) -> Unit)? = null,
    ) {
        try {
            onEnableCaptureBtn(false, null)

            val curRecording = recording
            if (curRecording != null) {
                // Stop the current recording session
                curRecording.stop()
                recording = null
                return
            }

            val mediaStoreOutputOptions = createVideoOutputOptions()

            recording = videoCapture.output
                .prepareRecording(application.applicationContext, mediaStoreOutputOptions)
                .apply {
                    if (PermissionChecker.checkSelfPermission(
                            application.applicationContext,
                            Manifest.permission.RECORD_AUDIO
                        ) == PermissionChecker.PERMISSION_GRANTED
                    ) {
                        withAudioEnabled()
                    }
                }.start(mainExecutors) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            val text = application.getString(R.string.stop_capture)
                            onEnableCaptureBtn(true, text)
                        }

                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                                onVideoSavedSuccess?.invoke(msg)
                            } else {
                                recording?.close()
                                recording = null
                                Timber.tag("ANDEBUG").e("Video capture ends with error: ${recordEvent.error}")
                            }

                            val btnTitle = application.getString(R.string.start_capture)
                            onEnableCaptureBtn(true, btnTitle)
                        }
                    }
                }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun createImageOutputOptions(): ImageCapture.OutputFileOptions {
        val contentResolver = application.contentResolver

        // Create time stamped name and MediaStore entry.
        val name = createImageOrVideoNameBasedOnTimeStamp()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, JPEG_MIME_TYPE)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, IMAGE_FOLDER_PATH)
            }
        }

        return ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
    }

    private fun createVideoOutputOptions(): MediaStoreOutputOptions {
        val contentResolver = application.contentResolver

        // create and start a new recording session
        val name = createImageOrVideoNameBasedOnTimeStamp() + ".mp4"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, MP4_MIME_TYPE)
            if (isAndroidLargerThanAndroid28()) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, VIDEO_FOLDER_PATH)
            }
        }

        return MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
    }

    private fun createImageOrVideoNameBasedOnTimeStamp() = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    private fun isAndroidLargerThanAndroid28() = Build.VERSION.SDK_INT > Build.VERSION_CODES.P

}