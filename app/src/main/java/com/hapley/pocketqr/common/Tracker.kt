package com.hapley.pocketqr.common

import android.os.Bundle
import androidx.annotation.StringDef
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param.*
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.hapley.pocketqr.features.barcode.domain.getBarcodeTypeName
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem

class Tracker {
    fun recordException(message: String, exception: Exception) {
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

    fun trackScreen(className: String, @ScreenName screenName: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_CLASS, className)
            putString(SCREEN_NAME, screenName)
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun share(barcodeItem: BarcodeItem) {
        val bundle = Bundle().apply {
            putString(ITEM_NAME, barcodeItem.title)
            putString(CONTENT_TYPE, barcodeItem.barcodeType.getBarcodeTypeName())
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    fun tutorialBegin() {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    fun tutorialComplete() {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
    }

    fun search(term: String) {
        if (term.isNotBlank()) {
            val bundle = Bundle().apply {
                putString(SEARCH_TERM, term)
            }
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }
    }

    fun selectContent(barcodeItem: BarcodeItem){
        val bundle = Bundle().apply {
            putString(ITEM_NAME, barcodeItem.title)
            putString(CONTENT_TYPE, barcodeItem.barcodeType.getBarcodeTypeName())
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

    }
}

const val SCREEN_APP_INTRO = "AppIntro"
const val SCREEN_SCANNER = "Scanner"
const val SCREEN_HISTORY = "History"
const val SCREEN_DETAIL = "Detail"
const val SCREEN_BOTTOM_SHEET = "BottomSheet"
const val SCREEN_SETTINGS = "Settings"
const val SCREEN_UNKNOWN = "Unknown"

@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [SCREEN_APP_INTRO, SCREEN_SCANNER, SCREEN_HISTORY, SCREEN_DETAIL, SCREEN_BOTTOM_SHEET, SCREEN_SETTINGS, SCREEN_UNKNOWN], open = false)
annotation class ScreenName