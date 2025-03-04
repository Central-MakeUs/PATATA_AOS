package com.cmc.patata

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.common.util.DeepLinkUtil
import com.cmc.patata.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    GlobalNavigation {

    private lateinit var binding: ActivityMainBinding

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragment_container_main) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navHostFragment.navController.addOnDestinationChangedListener(this)
        initBottomNavigation()

    }

    private fun initBottomNavigation() {
        INPUT_METHOD_SERVICE
        with(binding.bottomNavigationMain) {
            setupWithNavController(navHostFragment.navController)
            itemIconTintList = null
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // TODO : 화면별 Bottom Navigation Visible 여부 확인 후 처리

        when (destination.id) {
            com.cmc.presentation.R.id.HomeFragment,
            com.cmc.presentation.R.id.AroundMeFragment,
            com.cmc.presentation.R.id.ArchiveFragment,
            com.cmc.presentation.R.id.MyFragment
             -> { setBottomNavVisibility(true) }
            else -> { setBottomNavVisibility(false) }
        }
    }

    private fun setBottomNavVisibility(isVisible: Boolean) {
        binding.bottomNavigationMain.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    /*
    * Bottom Navigation 에 포함 되지 않은, Feature 이동을 담당
     */

    override fun navigateOnBoarding() { navigate(R.id.nav_onboarding) }
    override fun navigateLogin() { navigate(R.id.nav_login) }
    override fun navigateHome() { navigate(R.id.nav_home) }
    override fun navigateSearch() { navigate(R.id.nav_search) }
    override fun navigateSpotDetail(spotId: Int) {
        navigate(R.id.nav_spot_detail, Bundle().apply { putInt(NavigationKeys.SpotDetail.ARGUMENT_SPOT_ID, spotId) })
    }
    override fun navigateCategorySpots(categoryId: Int) {
        navigate(
            R.id.nav_category_spots,
            Bundle().apply { putInt(NavigationKeys.Category.ARGUMENT_CATEGORY_ID, categoryId) })
    }
    override fun navigateReport(reportType: Int, targetId: Int) {
        navigate(R.id.nav_report, Bundle().apply {
            putInt(NavigationKeys.Report.ARGUMENT_REPORT_TYPE, reportType)
            putInt(NavigationKeys.Report.ARGUMENT_REPORT_TARGET_ID, targetId)
        })
    }
    override fun navigateSelectLocation(latitude: Double, longitude: Double, isEdit: Boolean) {
        navigate(R.id.nav_select_location, Bundle().apply {
            putBoolean(NavigationKeys.Location.ARGUMENT_IS_EDIT, isEdit)
            putDouble(NavigationKeys.Location.ARGUMENT_LATITUDE, latitude)
            putDouble(NavigationKeys.Location.ARGUMENT_LONGITUDE, longitude)
        })
    }
    override fun navigateAddSpot(addressName: String, latitude: Double, longitude: Double) {
        navigate(DeepLinkUtil.createAddSpotUri(addressName, latitude, longitude))
    }
    override fun navigateWebView(url: String) {
        navigate(R.id.nav_web_view, Bundle().apply {
            putString(NavigationKeys.WebView.ARGUMENT_WEB_VIEW_URL, url)
        })
    }

    private fun navigate(navId: Int, bundle: Bundle? = null) {
        navHostFragment.navController.navigate(navId, bundle)
    }
    private fun navigate(uri: Uri) {
        navHostFragment.navController.navigate(uri)
    }
}