package com.hapley.pocketqr.common

import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.annotation.StringDef
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param.CONTENT_TYPE
import com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_CLASS
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME
import com.google.firebase.analytics.FirebaseAnalytics.Param.SEARCH_TERM
import com.google.firebase.analytics.FirebaseAnalytics.Param.VALUE
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.hapley.pocketqr.features.barcode.domain.getBarcodeTypeName
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.SortMode
import javax.inject.Inject

class Tracker @Inject constructor() {
    companion object {
        const val EVENT_COPY = "copy_to_clipboard"
        const val EVENT_DELETE = "delete"
        const val EVENT_FAVORITE = "favorite"
        const val EVENT_SCAN = "scan"
        const val EVENT_SORT = "sort"
        const val EVENT_ZOOM = "zoom"

        const val ZOOM_IN = "zoom_in"
        const val ZOOM_OUT = "zoom_out"

        const val ADD_FAVORITE = "add_favorite"
        const val REMOVE_UNFAVORITE = "remove_favorite"
    }

    fun recordException(message: String, exception: Exception) {
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

    private fun basicBundle(barcodeItem: BarcodeItem): Bundle = Bundle().apply {
        putString(ITEM_NAME, barcodeItem.title)
        putString(CONTENT_TYPE, barcodeItem.barcodeType.getBarcodeTypeName())
    }

    fun copy(barcodeItem: BarcodeItem) {
        val bundle = basicBundle(barcodeItem)
        Firebase.analytics.logEvent(EVENT_COPY, bundle)
    }

    fun delete(barcodeItem: BarcodeItem) {
        val bundle = basicBundle(barcodeItem)
        Firebase.analytics.logEvent(EVENT_DELETE, bundle)
    }

    fun favorite(barcodeItem: BarcodeItem, isFavorite: Boolean) {
        val bundle = basicBundle(barcodeItem)
        val paramValue = if (isFavorite) ADD_FAVORITE else REMOVE_UNFAVORITE

        bundle.apply {
            putString(VALUE, paramValue)
        }
        Firebase.analytics.logEvent(EVENT_FAVORITE, bundle)
    }

    fun scan(barcodeItem: BarcodeItem) {
        val bundle = basicBundle(barcodeItem)
        Firebase.analytics.logEvent(EVENT_SCAN, bundle)
    }

    fun sort(@SortMode sortMode: String) {
        val bundle = Bundle().apply {
            putString(VALUE, sortMode)
        }

        Firebase.analytics.logEvent(EVENT_SORT, bundle)
    }

    private var zoomValue = 0f
    fun zoom(@FloatRange(from = 0.0, to = 1.0) linearZoom: Float) {
        val zoomType = if (linearZoom > zoomValue) ZOOM_IN else ZOOM_OUT
        zoomValue = linearZoom

        val bundle = Bundle().apply {
            putString(VALUE, zoomType)
        }

        Firebase.analytics.logEvent(EVENT_ZOOM, bundle)
    }

    fun search(term: String) {
        if (term.isNotBlank()) {
            val bundle = Bundle().apply {
                putString(SEARCH_TERM, term)
            }
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }
    }

    fun selectContent(barcodeItem: BarcodeItem) {
        val bundle = Bundle().apply {
            putString(ITEM_NAME, barcodeItem.title)
            putString(CONTENT_TYPE, barcodeItem.barcodeType.getBarcodeTypeName())
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun share(barcodeItem: BarcodeItem) {
        val bundle = basicBundle(barcodeItem)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    fun trackScreen(className: String, @ScreenName screenName: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_CLASS, className)
            putString(SCREEN_NAME, screenName)
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun tutorialBegin() {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    fun tutorialComplete() {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
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
