package com.annguyenhoang.androidviewobjectdetectionwitharcore.common.fragment_binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.Constants

abstract class DataBindingFragment<T : ViewDataBinding> : Fragment() {

    abstract val layoutId: Int
    private val _layoutId: Int
        @LayoutRes
        get() = layoutId

    private var _binding: T? = null
    val binding
        get() = requireNotNull(
            _binding,
            lazyMessage = {
                Constants.CANNOT_BIND_FRAGMENT
            }
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, _layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initControls()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun initViews() = Unit
    open fun initControls() = Unit

}