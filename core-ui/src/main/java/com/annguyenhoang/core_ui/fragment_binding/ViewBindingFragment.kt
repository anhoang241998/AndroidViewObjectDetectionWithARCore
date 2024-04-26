package com.annguyenhoang.core_ui.fragment_binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.annguyenhoang.core.utils.Constants.CANNOT_BIND_FRAGMENT

abstract class ViewBindingFragment<T : ViewBinding> : Fragment() {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T

    open val enableBackPressed = true
    private var _binding: T? = null
    val binding: T
        get() = requireNotNull(
            _binding,
            lazyMessage = {
                CANNOT_BIND_FRAGMENT
            }
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater(inflater, container, false)
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