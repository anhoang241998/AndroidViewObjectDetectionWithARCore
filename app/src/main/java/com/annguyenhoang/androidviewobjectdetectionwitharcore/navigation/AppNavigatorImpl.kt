package com.annguyenhoang.androidviewobjectdetectionwitharcore.navigation

import androidx.navigation.NavController
import com.annguyenhoang.androidviewobjectdetectionwitharcore.R
import com.annguyenhoang.core_ui.navigation.AppNavigator

class AppNavigatorImpl : AppNavigator {
    private var navController: NavController? = null

    override fun setUpRootNavController(navController: NavController) {
        this.navController = navController
    }

    override fun moveToMLKitObjectDetectionFragment() {
        navController?.navigate(R.id.action_demoListFragment_to_MLKitObjectDetectionFragment)
    }

    override fun moveToCameraXWithYOLOV8Fragment() {
        navController?.navigate(R.id.action_demoListFragment_to_cameraXWithYOLOV8Fragment)
    }

}