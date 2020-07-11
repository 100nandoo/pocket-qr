package com.hapley.pocketqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.hapley.pocketqr.util.BuildUtil
import kotlinx.android.synthetic.main.activity_main.*
import me.toptas.fancyshowcase.FancyShowCaseView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        
        if(BuildUtil.isPro.not()){
            initAds()
        }
    }

    private fun initAds() {
        MobileAds.initialize(this)
    }

    private fun initToolbar() {
        setSupportActionBar(main_toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        main_toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (FancyShowCaseView.isVisible(this)) {
            FancyShowCaseView.hideCurrent(this)
        } else {
            super.onBackPressed()
        }
    }
}