package com.cmc.patata

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cmc.patata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

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
        with(binding.bottomNavigationMain) {
            setupWithNavController(navHostFragment.navController)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // TODO : 화면별 바텀 내비게이션 Visible 여부 확인 후 처리
1
        when (destination.id) {
            com.cmc.presentation.R.id.FirstFragment,
            com.cmc.presentation.R.id.SecondFragment
             -> { setBottomNavVisibility(true) }
            else -> { setBottomNavVisibility(false) }
        }
    }

    private fun setBottomNavVisibility(isVisible: Boolean) {
        binding.bottomNavigationMain.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}