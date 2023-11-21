package com.digeltech.discountone

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.digeltech.discountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.discountone.databinding.ActivityMainBinding
import com.digeltech.discountone.ui.common.SignUpDialogFragment
import com.digeltech.discountone.ui.home.KEY_HOMEPAGE_DATA
import com.digeltech.discountone.util.log
import com.digeltech.discountone.util.view.toast
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val UPDATE_CODE = 100

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferencesDataSource
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var referrerClient: InstallReferrerClient

    @Inject
    lateinit var logger: AppEventsLogger

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            toast("Notifications permission granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferencesDataSource(baseContext)
        Hawk.delete(KEY_HOMEPAGE_DATA)

        if (!prefs.isLogin()) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val dialogFragment = SignUpDialogFragment()
                dialogFragment.show(supportFragmentManager, dialogFragment.tag)
            }, 3 * 60 * 1000) // 3 minutes
        }

        appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        logger = AppEventsLogger.newLogger(this)

        checkForUpdates()
        setupNavigation()
        setupFCM()
        registerReceiver()
    }

    private fun registerReceiver() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        val referrerUrl: String = response.installReferrer

                        val uri = Uri.parse(referrerUrl)
                        val referrerCode = uri.getQueryParameter("referrer")

                        log("Referrer code: $referrerCode")

                        if (referrerCode != null) {
                            val bundle = Bundle()
                            bundle.putString("referrer", referrerCode)
                            Firebase.analytics.logEvent("install_referrer", bundle)
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        toast("Referrer feature not supported.")
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun setupFCM() {
        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        askNotificationPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                log("Fetching FCM registration token failed ${task.exception}")
                return@OnCompleteListener
            }

            val token = task.result
            log("Firebase token $token")
        })
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
    }

    private fun checkForUpdates() {
        val listener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.registerListener(listener)
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    UPDATE_CODE
                )
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.navHostFragment),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.black))
            show()
        }
    }

    private fun setupNavigation() {
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)

        intent.apply {
            checkNotification(navGraph, navController)
            checkDeeplink(navGraph, navController)
            navController.graph = navGraph
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.categoriesFragment,
                R.id.categoryAndShopFragment,
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

    private fun Intent.checkNotification(
        navGraph: NavGraph,
        navController: NavController
    ) {
        if (extras != null && extras!!.containsKey("fragment")) {
            val checkLogin = getStringExtra("checkLogin").toBoolean()
            if (!checkLogin || (checkLogin && prefs.isLogin())) {
                when (getStringExtra("fragment")) {
                    "ShopsFragment" -> {
                        navGraph.setStartDestination(R.id.shopsFragment)
                    }
                    "CategoriesFragment" -> {
                        navGraph.setStartDestination(R.id.categoriesFragment)
                    }
                    "CategoryFragment", "ShopFragment" -> {
                        val id = getIntExtra("id", 0)
                        val title = getStringExtra("title")
                        val slug = getStringExtra("slug")
                        val isFromCategory = getStringExtra("isFromCategory").toBoolean()

                        if (isFromCategory)
                            navGraph.setStartDestination(R.id.categoriesFragment)
                        else
                            navGraph.setStartDestination(R.id.shopsFragment)

                        val bundle = Bundle().apply {
                            putInt("id", id)
                            putString("title", title)
                            putString("slug", slug)
                            putBoolean("isFromCategory", isFromCategory)
                        }
                        navController.graph = navGraph
                        navController.navigate(R.id.categoryAndShopFragment, bundle)
                    }
                    "DealFragment" -> {
                        val id = getStringExtra("id")?.toInt() ?: 0
                        val bundle = Bundle().apply {
                            putParcelable("deal", null)
                            putInt("dealId", id)
                        }
                        navGraph.setStartDestination(R.id.homeFragment)
                        navController.graph = navGraph
                        navController.navigate(R.id.dealFragment, bundle)
                    }
                    else -> {
                        navGraph.setStartDestination(R.id.splashFragment)
                    }
                }
            } else {
                navGraph.setStartDestination(R.id.splashFragment)
            }
        } else {
            navGraph.setStartDestination(R.id.splashFragment)
        }
    }

    private fun checkDeeplink(navGraph: NavGraph, navController: NavController) {
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == intent.action && appLinkData != null) {
            val pathSegments = appLinkData.pathSegments
            val userId = appLinkData.pathSegments.lastOrNull()

            if (pathSegments.contains("user_update_password") && !prefs.isLogin()) {
                navGraph.setStartDestination(R.id.homeFragment)
                navController.graph = navGraph

                val bundle = Bundle().apply {
                    putString("userId", userId)
                }
                navController.graph = navGraph
                navController.navigate(R.id.recoveryPasswordFragment, bundle)
            } else {
                navGraph.setStartDestination(R.id.splashFragment)
            }
        }
    }

    private fun logOpenEvent(screenName: String?, screenClass: String?) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}