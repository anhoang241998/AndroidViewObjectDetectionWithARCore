package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.fragment_binding.ViewBindingFragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.FragmentDemoListBinding
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.model.DemoTypeUIState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DemoListFragment : ViewBindingFragment<FragmentDemoListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDemoListBinding
        get() = FragmentDemoListBinding::inflate

    private val viewModel: DemoListViewModel by viewModel()
    private lateinit var demoListAdapter: DemoListAdapter

    override fun initViews() {
        super.initViews()
        changeStatusBarColor()
        setUpPadding()
        setUpRecyclerView()
        observeDemoList()
    }

    override fun initControls() {
        super.initControls()
        demoListAdapter.setOnDemoItemTapped { demoItem ->
            when (demoItem.demoType) {
                DemoTypeUIState.MLKIT_OBJECT_DETECTION -> {
                    val mlKitObjectDetection = DemoListFragmentDirections
                        .actionDemoListFragmentToMLKitObjectDetectionFragment()
                    findNavController().navigate(mlKitObjectDetection)
                }

                DemoTypeUIState.CAMERAX_WITH_YOLO_V8 -> {
                    val cameraXWithYOLOv8 = DemoListFragmentDirections
                        .actionDemoListFragmentToCameraXWithYOLOV8Fragment()
                    findNavController().navigate(cameraXWithYOLOv8)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        binding.rvDemoList.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View) = Unit

            override fun onViewDetachedFromWindow(p0: View) {
                binding.rvDemoList.adapter = null
            }
        })
    }

    private fun observeDemoList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.demoList.collect {
                    demoListAdapter.submitList(it.toMutableList())
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvDemoList.apply {
            demoListAdapter = DemoListAdapter()
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            adapter = demoListAdapter
        }
    }

    private fun setUpPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.demoListContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun changeStatusBarColor() {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            ),
        )
    }

}