package com.annguyenhoang.core_ui.permission

import androidx.activity.result.ActivityResultLauncher

interface PermissionHandler {
    val requiredCameraPermissions: Array<String>

    fun allPermissionsGranted(): Boolean
    fun requestPermissions(permissionsLauncher: ActivityResultLauncher<Array<String>>)
}