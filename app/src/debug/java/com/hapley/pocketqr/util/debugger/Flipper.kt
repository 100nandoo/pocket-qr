package com.hapley.pocketqr.util.debugger

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.leakcanary.LeakCanaryFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader

class Flipper(app: Application) {
    init {
        SoLoader.init(app, false)
        if(FlipperUtils.shouldEnableFlipper(app)){
            AndroidFlipperClient.getInstance(app).apply {
                addPlugin(InspectorFlipperPlugin(app, DescriptorMapping.withDefaults()))
                addPlugin(DatabasesFlipperPlugin(app))
                addPlugin(SharedPreferencesFlipperPlugin(app))
                addPlugin(LeakCanaryFlipperPlugin())
                start()
            }
        }
    }
}