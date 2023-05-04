package com.digeltech.appdiscountone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferencesDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferencesDataSource(baseContext)

        val navFragment =
            supportFragmentManager.findFragmentById(R.id.navigationHost) as NavHostFragment
        val navController = navFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.categoriesFragment,
                R.id.dealsFragment,
                R.id.shopsFragment,
                R.id.couponsFragment -> {
                    binding.bottomNavMenu.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavMenu.visibility = View.GONE
                }
            }.apply {
                logOpenEvent(
                    destination.label as String?,
                    destination.label as String?
                )
            }

            binding.bottomNavMenu.setupWithNavController(navController)
            binding.bottomNavMenu.setOnItemSelectedListener { menuItem ->
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(menuItem.itemId, false)
                    .build()
                when (menuItem.itemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.homeFragment, null, navOptions)
                        true
                    }
                    R.id.categoriesFragment -> {
                        navController.navigate(R.id.categoriesFragment, null, navOptions)
                        true
                    }
                    R.id.dealsFragment -> {
                        navController.navigate(R.id.dealsFragment, null, navOptions)
                        true
                    }
                    R.id.shopsFragment -> {
                        navController.navigate(R.id.shopsFragment, null, navOptions)
                        true
                    }
                    R.id.couponsFragment -> {
                        navController.navigate(R.id.couponsFragment, null, navOptions)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun logOpenEvent(screenName: String?, screenClass: String?) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }
}