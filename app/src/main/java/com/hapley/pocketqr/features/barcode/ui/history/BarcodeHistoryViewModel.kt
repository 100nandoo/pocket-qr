package com.hapley.pocketqr.features.barcode.ui.history

import androidx.core.content.edit
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.SettingsFragment

class BarcodeHistoryViewModel(barcodeUseCase: BarcodeUseCase, private val appPreferences: AppPreferences) : ViewModel() {

    var openBarcodeHistoryFirst : Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_OPEN_HISTORY_FIRST, false)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_OPEN_HISTORY_FIRST, value) }
        }

    var showTutorial: Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, true)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, value) }
        }

    val barcodesLiveData = Transformations.map(barcodeUseCase.getAllLiveData()) { barcodes -> barcodes.map { BarcodeItem(it) } }
}
