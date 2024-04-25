package com.annguyenhoang.core_ui.activity_binding

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class ViewBindingActivity<T : ViewBinding> : AppCompatActivity() {

    abstract val bindingInflater: (LayoutInflater) -> T

    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        initViews()
        initControls()
    }

    abstract fun initViews()
    open fun initControls() = Unit
}