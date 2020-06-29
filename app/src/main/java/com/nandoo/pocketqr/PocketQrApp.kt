package com.nandoo.pocketqr

import android.app.Application
import com.nandoo.pocketqr.common.Logging
import com.nandoo.pocketqr.di.appModule
import com.nandoo.pocketqr.di.barcodeModule
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
    }
}
