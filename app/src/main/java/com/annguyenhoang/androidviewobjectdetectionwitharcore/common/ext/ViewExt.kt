package com.annguyenhoang.androidviewobjectdetectionwitharcore.common.ext

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.setOnThrottleClicked(
    enabled: Boolean = true,
    throttlePeriod: Long = 500L,
    onClick: (View) -> Unit
) {
    if (enabled.not()) return
    var lastClickTime = 0L

    this.setOnClickListener {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > throttlePeriod) {
            lastClickTime = now
            onClick(it)
        }
    }
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}