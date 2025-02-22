package com.cmc.common.base

interface GlobalNavigation {
    fun navigateOnBoarding()
    fun navigateLogin()
    fun navigateHome()
    fun navigateSearch()
    fun navigateSpotDetail(spotId: Int)
    fun navigateCategorySpots(categoryId: Int)
    fun navigateReport(reportType: Int, targetId: Int)
    fun navigateSelectLocation(latitude: Double, longitude: Double, isEdit: Boolean = false)
    fun navigateAddSpot(addressName: String, latitude: Double, longitude: Double)
}