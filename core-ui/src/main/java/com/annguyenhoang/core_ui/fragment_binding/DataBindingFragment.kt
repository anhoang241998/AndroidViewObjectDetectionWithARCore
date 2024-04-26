package com.annguyenhoang.core_ui.fragment_binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.annguyenhoang.core.utils.Constants

abstract class DataBindingFragment<T : ViewDataBinding> : Fragment() {

    open val enableBackPressed = true
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

        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(enableBackPressed) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun initViews() = Unit
    open fun initControls() = Unit
    open fun onBackPressed() = Unit

}