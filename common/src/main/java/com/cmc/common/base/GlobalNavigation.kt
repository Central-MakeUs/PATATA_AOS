package com.cmc.common.base

interface GlobalNavigation {
    fun navigateOnBoarding()
    fun navigateLogin()
    fun navigateHome()
    fun navigateSearch()
    fun navigateSpotDetail(spotId: Int)
    fun navigateCategorySpots(categoryId: Int)
    fun navigateReport(reportType: Int, targetId: Int)
}