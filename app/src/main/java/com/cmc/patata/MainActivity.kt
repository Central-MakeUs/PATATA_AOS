package com.cmc.patata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cmc.common.base.GlobalNavigation
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
            com.cmc.presentation.R.id.HomeFragment
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

    override fun navigateOnBoarding() { navigate(R.id.navigate_onboarding) }
    override fun navigateLogin() { navigate(R.id.navigate_login) }
    override fun navigateHome() { navigate(R.id.nav_home) }
    override fun navigateSearch() { navigate(R.id.nav_search) }

    private fun navigate(navId: Int) {
        navHostFragment.navController.navigate(navId)
    }
}