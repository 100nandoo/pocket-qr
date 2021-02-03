package com.hapley.pocketqr

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.common.Logging
import com.hapley.pocketqr.di.MockDataGenerator
import com.hapley.pocketqr.ui.settings.FOLLOW_SYSTEM
import com.hapley.pocketqr.ui.settings.Mapper
import com.hapley.pocketqr.ui.settings.SettingsFragment
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.debugger.Flipper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PocketQrApp : Application() {

    @Inject
    lateinit var mockDataGenerator: MockDataGenerator

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate() {
        super.onCreate()

        if (BuildUtil.isRelease.not()) {
            mockDataGenerator.inject()
        }

        Logging.init()

        Flipper(this)

        setNightMode()
    }

    private fun setNightMode() {
        val nightMode = appPreferences.settings.getString(SettingsFragment.NIGHT_MODE, FOLLOW_SYSTEM) ?: FOLLOW_SYSTEM
        val nightModeStatic = Mapper.nightModetoNightModeStatic(nightMode)
        AppCompatDelegate.setDefaultNightMode(nightModeStatic)
    }
}
