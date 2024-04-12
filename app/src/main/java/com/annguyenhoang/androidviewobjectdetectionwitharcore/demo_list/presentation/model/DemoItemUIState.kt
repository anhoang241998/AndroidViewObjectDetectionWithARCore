package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.model

data class DemoItemUIState(
    val demoId: Int = 0,
    val demoName: String,
    val demoType: DemoTypeUIState
)