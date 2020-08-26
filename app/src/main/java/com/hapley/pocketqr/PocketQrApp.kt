package com.hapley.pocketqr

import android.app.Application
import com.hapley.pocketqr.common.Logging
import com.hapley.pocketqr.di.appModule
import com.hapley.pocketqr.di.barcodeModule
import com.hapley.pocketqr.util.debugger.Flipper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class PocketQrApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PocketQrApp)
            modules(listOf(appModule, barcodeModule))
        }

        Logging.init()

        Flipper(this)
    }
}
