package com.hapley.pocketqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.hapley.pocketqr.di.MockDataGenerator
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.activity_main.*
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {


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

    override fun onBackPressed() {
        if (FancyShowCaseView.isVisible(this)) {
            FancyShowCaseView.hideCurrent(this)
        } else {
            super.onBackPressed()
        }
    }
}