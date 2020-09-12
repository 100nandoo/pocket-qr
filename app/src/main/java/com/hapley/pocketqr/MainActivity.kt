package com.hapley.pocketqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.toOptional
import com.google.android.gms.ads.MobileAds
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    init {
        lifecycleScope.launch {
            whenStarted {
                val result = try {
                    pocketQrUtil.initCustomTabConnection(this@MainActivity)
                } catch (E: Exception) {
                    null
                }.toOptional()

                if (result is Some) {
                    connection = result.value.first.toOptional()
                    session = result.value.second.toOptional()
                }
            }
        }
    }

    private val pocketQrUtil: PocketQrUtil by inject()

    private var connection: Optional<CustomTabsServiceConnection> = None
    var session: Optional<CustomTabsSession> = None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()

        initAds()
    }

    private fun initAds() {
        if (BuildUtil.isPro.not()) {
            MobileAds.initialize(this)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(main_toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.barcode_scanner_fragment,
                R.id.barcode_history_fragment,
                R.id.settings_fragment
            )
        )
        main_toolbar.setupWithNavController(navController, appBarConfiguration)
        bottom_nav.setupWithNavController(navController)
    }

    override fun onStop() {
        super.onStop()
//        val connectionTemp = connection
//        if (connectionTemp is Some) {
//            unbindService(connectionTemp.value)
//            connection = None
//            session = None
//        }
    }
}