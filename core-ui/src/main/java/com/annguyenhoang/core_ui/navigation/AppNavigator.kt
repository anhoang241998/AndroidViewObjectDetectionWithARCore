package com.annguyenhoang.core_ui.navigation

import androidx.navigation.NavController

interface AppNavigator {
    fun setUpRootNavController(navController: NavController)
    fun moveToMLKitObjectDetectionFragment()
    fun moveToCameraXWithYOLOV8Fragment()
}