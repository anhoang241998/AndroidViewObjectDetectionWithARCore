package com.annguyenhoang.androidviewobjectdetectionwitharcore.navigation

import androidx.navigation.NavController
import com.annguyenhoang.androidviewobjectdetectionwitharcore.R
import com.annguyenhoang.core_ui.navigation.AppNavigator
import com.annguyenhoang.core_ui.navigation.Navigator

class AppNavigatorImpl : AppNavigator {
    override var rootController: NavController? = null

    override fun setUpRootNavController(navController: NavController) {
        rootController = navController
    }

    override fun moveToMLKitObjectDetectionFragment() {
        rootController?.navigate(R.id.action_demoListFragment_to_MLKitObjectDetectionFragment)
    }

    override fun moveToCameraXWithYOLOV8Fragment() {
        rootController?.navigate(R.id.action_demoListFragment_to_cameraXWithYOLOV8Fragment)
    }

}