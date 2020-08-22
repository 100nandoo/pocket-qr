package com.hapley.pocketqr.features.barcode.ui.history

import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.RECENT
import com.hapley.pocketqr.ui.settings.SettingsFragment
import com.hapley.pocketqr.ui.settings.SortMode
import kotlinx.coroutines.launch

class BarcodeHistoryViewModel(private val barcodeUseCase: BarcodeUseCase, private val appPreferences: AppPreferences) : ViewModel() {

    var showTutorial: Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, true)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, value) }
        }

    @SortMode
    var sortMode: String
        get() = appPreferences.settings.getString(SettingsFragment.BARCODE_HISTORY_SORT, RECENT) ?: RECENT
        set(value) {
            appPreferences.settings.edit { putString(SettingsFragment.BARCODE_HISTORY_SORT, value) }
        }

    val barcodesLiveData = Transformations.map(barcodeUseCase.getAllLiveData()) { barcodes -> barcodes.map { BarcodeItem(it) } }

    lateinit var selectedItemWithPosition: Triple<View, BarcodeItem, Int>

    fun updateFavoriteFlag() {
        viewModelScope.launch {
            val isFavorite = selectedItemWithPosition.second.isFavorite.not()
            barcodeUseCase.updateFavorite(selectedItemWithPosition.second.id.toInt(), isFavorite)
        }
    }

    fun incrementClickCount(barcodeId: Int){
        viewModelScope.launch {
            barcodeUseCase.incrementClickCount(barcodeId)
        }
    }

    fun deleteBarcode(barcodeId: Int){
        viewModelScope.launch {
            barcodeUseCase.deleteBarcode(barcodeId)
        }
    }
}
