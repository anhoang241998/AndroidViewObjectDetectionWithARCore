package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
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
    private val demoListAdapter: DemoListAdapter by lazy {
        DemoListAdapter()
    }

    override fun initViews() {
        super.initViews()
        setUpRecyclerView()
        observeDemoList()
    }

    override fun initControls() {
        super.initControls()
        demoListAdapter.setOnDemoItemTapped { demoItem ->
            when (demoItem.demoType) {
                DemoTypeUIState.CAMERAX_WITH_YOLO_V8 -> {
                    val cameraXWithYOLOv8 = DemoListFragmentDirections.actionDemoListFragmentToCameraXWithYOLOV8Fragment()
                    findNavController().navigate(cameraXWithYOLOv8)
                }
            }
        }
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
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            adapter = demoListAdapter
        }
    }

}