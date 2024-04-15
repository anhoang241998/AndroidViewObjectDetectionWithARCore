package com.annguyenhoang.androidviewobjectdetectionwitharcore.common.permission

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class CameraPermissionHandlerImpl(
    private val application: Application
) : PermissionHandler {

    override val requiredCameraPermissions: Array<String> = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    override fun allPermissionsGranted(): Boolean {
        val context = application.applicationContext
        val permissionGrantedStatus = PackageManager.PERMISSION_GRANTED
        val allPermissionGranted = requiredCameraPermissions.all { permission ->
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                permission
            )
            permissionStatus == permissionGrantedStatus
        }
        return allPermissionGranted
    }

    override fun requestPermissions(permissionsLauncher: ActivityResultLauncher<Array<String>>) {
        permissionsLauncher.launch(requiredCameraPermissions)
    }

}