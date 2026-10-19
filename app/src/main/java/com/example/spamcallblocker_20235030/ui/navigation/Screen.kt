package com.example.spamcallblocker_20235030.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Tổng quan", Icons.Default.Dashboard)
    object BrandList : Screen("brand_list", "Thương hiệu", Icons.Default.Business)
    object History : Screen("history", "Lịch sử", Icons.Default.History)
    object Settings : Screen("settings", "Cài đặt", Icons.Default.Settings)
    object BrandDetail : Screen("brand_detail/{brandId}", "Chi tiết thương hiệu") {
        fun createRoute(brandId: Long) = "brand_detail/$brandId"
    }

    companion object {
        val bottomNavItems = listOf(Dashboard, BrandList, History, Settings)
    }
}
