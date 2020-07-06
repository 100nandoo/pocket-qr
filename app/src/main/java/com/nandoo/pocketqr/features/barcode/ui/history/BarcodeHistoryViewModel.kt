package com.nandoo.pocketqr.features.barcode.ui.history

import androidx.core.content.edit
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nandoo.pocketqr.common.AppPreferences
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import com.nandoo.pocketqr.features.barcode.ui.BarcodeItem
import com.nandoo.pocketqr.ui.settings.SettingsFragment

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
