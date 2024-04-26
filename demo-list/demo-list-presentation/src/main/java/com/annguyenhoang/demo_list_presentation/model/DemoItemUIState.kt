package com.annguyenhoang.demo_list_presentation.model

data class DemoItemUIState(
    val demoId: Int = 0,
    val demoName: String,
    val demoType: DemoTypeUIState
)