package com.nandoo.pocketqr.common

import com.nandoo.pocketqr.util.BuildUtil
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
