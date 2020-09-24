package com.hapley.pocketqr.common

import android.os.Bundle
import androidx.annotation.StringDef
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class Tracker {
    fun recordException(message: String, exception: Exception) {
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

    fun trackScreen(className: String, @ScreenName screenName: String) {
        Timber.d("Track screen with className = $className screenName = $screenName")
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}

const val SCREEN_SCANNER = "Scanner"
const val SCREEN_HISTORY = "History"
const val SCREEN_DETAIL = "Detail"
const val SCREEN_BOTTOM_SHEET = "BottomSheet"
const val SCREEN_SETTINGS = "Settings"
const val SCREEN_UNKNOWN = "Unknown"

@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [SCREEN_SCANNER, SCREEN_HISTORY, SCREEN_DETAIL, SCREEN_BOTTOM_SHEET, SCREEN_SETTINGS, SCREEN_UNKNOWN], open = false)
annotation class ScreenName