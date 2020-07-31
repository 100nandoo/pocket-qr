package com.hapley.pocketqr.features.barcode.ui.history

import androidx.core.content.edit
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.SettingsFragment
import kotlinx.coroutines.launch

class BarcodeHistoryViewModel(private val barcodeUseCase: BarcodeUseCase, private val appPreferences: AppPreferences) : ViewModel() {

    var showTutorial: Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, true)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, value) }
        }

    val barcodesLiveData = Transformations.map(barcodeUseCase.getAllLiveData()) { barcodes -> barcodes.map { BarcodeItem(it) } }

    lateinit var selectedItemWithPosition: Pair<BarcodeItem, Int>

    fun updateFavoriteFlag() {
        viewModelScope.launch {
            val isFavorite = selectedItemWithPosition.first.isFavorite.not()
            barcodeUseCase.updateFavorite(selectedItemWithPosition.first.id.toInt(), isFavorite)
        }
    }
}
