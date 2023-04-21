package com.digeltech.appdiscountone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.ActivityMainBinding
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
            if (destination.id == R.id.homeFragment) {
                binding.bottomNavMenu.visibility = View.VISIBLE
            }
            if (destination.id == R.id.startFragment) {
                binding.bottomNavMenu.visibility = View.INVISIBLE
            }
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