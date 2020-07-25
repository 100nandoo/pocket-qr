package com.hapley.pocketqr.common

import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashReport {
    fun recordException(message: String, exception: Exception) {
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(exception)
    }
}
