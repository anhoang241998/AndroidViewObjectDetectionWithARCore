package com.annguyenhoang.core_ui.navigation

import androidx.navigation.NavController

interface Navigator {
    var rootController: NavController?

    fun setUpRootNavController(navController: NavController)
}