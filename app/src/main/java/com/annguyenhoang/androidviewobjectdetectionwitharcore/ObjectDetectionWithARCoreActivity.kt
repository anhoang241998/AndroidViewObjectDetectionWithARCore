package com.annguyenhoang.androidviewobjectdetectionwitharcore

import android.view.LayoutInflater
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.activity_binding.ViewBindingActivity
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.ActivityObjectDetectionWithArcoreBinding

class ObjectDetectionWithARCoreActivity : ViewBindingActivity<ActivityObjectDetectionWithArcoreBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityObjectDetectionWithArcoreBinding
        get() = ActivityObjectDetectionWithArcoreBinding::inflate
    private lateinit var navController: NavController

    override fun initViews() {
        setUpSystemPadding()
        setupNavController()
    }

    private fun setupNavController() {
        val navHostContainerId = binding.navHostFragmentContainer.id
        val navHostFragment = supportFragmentManager.findFragmentById(navHostContainerId) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setUpSystemPadding() {
        val mainId = binding.main.id
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(mainId)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}