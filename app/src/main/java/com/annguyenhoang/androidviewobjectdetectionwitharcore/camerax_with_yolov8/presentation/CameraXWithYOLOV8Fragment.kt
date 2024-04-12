package com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.fragment_binding.ViewBindingFragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.FragmentCameraxWithYolov8Binding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraXWithYOLOV8Fragment : ViewBindingFragment<FragmentCameraxWithYolov8Binding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCameraxWithYolov8Binding
        get() = FragmentCameraxWithYolov8Binding::inflate

    private val viewModel: CameraXWithYOLOV8ViewModel by viewModel()

    override fun initViews() {
        super.initViews()
        observeApplicationFPS()
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

}