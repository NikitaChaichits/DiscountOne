package com.digeltech.appdiscountone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferencesDataSource
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferencesDataSource(baseContext)
        auth = Firebase.auth

        val navFragment =
            supportFragmentManager.findFragmentById(R.id.navigationHost) as NavHostFragment
        val navController = navFragment.navController

        if (Firebase.auth.currentUser == null) {
            navController.navigate(R.id.startFragment)
            binding.bottomNavMenu.visibility = View.GONE
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                binding.bottomNavMenu.visibility = View.VISIBLE
            }
            if (destination.id == R.id.startFragment) {
                binding.bottomNavMenu.visibility = View.INVISIBLE
            }
        }

        binding.bottomNavMenu.setupWithNavController(navController)
    }
}