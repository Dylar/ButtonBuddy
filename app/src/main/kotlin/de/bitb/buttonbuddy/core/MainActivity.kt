package de.bitb.buttonbuddy.core

import android.app.Application
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import de.bitb.buttonbuddy.R

@HiltAndroidApp
class BuddyApp : Application()

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean =
        navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()

    private fun setupNavigation() {
//        navHostFragment.navController.setGraph(R.navigation.nav_graph)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navHostFragment.navController.navigateUp()
            }
        })
    }
}