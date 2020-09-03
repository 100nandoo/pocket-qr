package com.hapley.pocketqr

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.common.Logging
import com.hapley.pocketqr.di.MockDataGenerator
import com.hapley.pocketqr.di.appModule
import com.hapley.pocketqr.di.barcodeModule
import com.hapley.pocketqr.di.fakeModule
import com.hapley.pocketqr.ui.settings.FOLLOW_SYSTEM
import com.hapley.pocketqr.ui.settings.Mapper
import com.hapley.pocketqr.ui.settings.SettingsFragment
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.debugger.Flipper
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class PocketQrApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PocketQrApp)

            if (BuildUtil.isRelease) {
                modules(listOf(appModule, barcodeModule))
            } else {
                modules(listOf(appModule, barcodeModule, fakeModule))
                val mockDataGenerator: MockDataGenerator by inject()
                mockDataGenerator.inject()
            }
        }

        Logging.init()

        Flipper(this)

        setNightMode()
    }

    private fun setNightMode() {
        val appPreferences: AppPreferences by inject()

        val nightMode = appPreferences.settings.getString(SettingsFragment.NIGHT_MODE, FOLLOW_SYSTEM) ?: FOLLOW_SYSTEM
        val nightModeStatic = Mapper.nightModetoNightModeStatic(nightMode)
        AppCompatDelegate.setDefaultNightMode(nightModeStatic)
    }
}
