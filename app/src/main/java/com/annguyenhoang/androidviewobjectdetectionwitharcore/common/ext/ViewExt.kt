package com.annguyenhoang.androidviewobjectdetectionwitharcore.common.ext

import android.view.View

fun View.setOnThrottleClicked(throttlePeriod: Long = 500L, onClick: (View) -> Unit) {
    var lastClickTime = 0L

    this.setOnClickListener {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > throttlePeriod) {
            lastClickTime = now
            onClick(it)
        }
    }
}