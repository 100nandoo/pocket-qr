package com.hapley.pocketqr.common

import com.hapley.pocketqr.util.BuildUtil
import timber.log.Timber

object Logging {

    fun init() {
        when {
            BuildUtil.isDebug -> {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}
