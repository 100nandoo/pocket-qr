package com.hapley.pocketqr.ui.launcher

import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.ui.settings.SettingsFragment

class LauncherViewModel(private val appPreferences: AppPreferences): ViewModel() {

    var showTutorial: Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_APP_INTRODUCTION, true)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_APP_INTRODUCTION, value) }
        }
}