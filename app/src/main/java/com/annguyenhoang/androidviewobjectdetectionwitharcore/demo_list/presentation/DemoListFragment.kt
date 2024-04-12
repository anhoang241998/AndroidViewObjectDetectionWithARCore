package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.fragment_binding.ViewBindingFragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.databinding.FragmentDemoListBinding

class DemoListFragment : ViewBindingFragment<FragmentDemoListBinding>()  {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDemoListBinding
        get() = FragmentDemoListBinding::inflate

    override fun initControls() {
        super.initControls()
    }

}