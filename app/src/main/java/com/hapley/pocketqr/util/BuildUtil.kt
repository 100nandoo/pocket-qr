package com.hapley.pocketqr.util

import com.hapley.pocketqr.BuildConfig

object BuildUtil {
    const val isDebug: Boolean = BuildConfig.BUILD_TYPE == "debug"
    const val isRelease: Boolean = BuildConfig.BUILD_TYPE == "release"
    const val versionName = BuildConfig.VERSION_NAME
}
