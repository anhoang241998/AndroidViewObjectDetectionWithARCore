package com.annguyenhoang.androidviewobjectdetectionwitharcore

import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.activity_binding.ViewBindingActivity
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.ActivityObjectDetectionWithArcoreBinding

class ObjectDetectionWithARCoreActivity : ViewBindingActivity<ActivityObjectDetectionWithArcoreBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityObjectDetectionWithArcoreBinding
        get() = ActivityObjectDetectionWithArcoreBinding::inflate
    private lateinit var navController: NavController

    override fun initViews() {
        setupNavController()
    }

    private fun setupNavController() {
        val navHostContainerId = binding.navHostFragmentContainer.id
        val navHostFragment = supportFragmentManager.findFragmentById(navHostContainerId) as NavHostFragment
        navController = navHostFragment.navController
    }
}