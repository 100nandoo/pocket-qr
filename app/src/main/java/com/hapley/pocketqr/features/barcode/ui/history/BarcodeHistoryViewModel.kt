package com.hapley.pocketqr.features.barcode.ui.history

import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.ALPHABETICAL
import com.hapley.pocketqr.ui.settings.MOST_FREQUENT
import com.hapley.pocketqr.ui.settings.RECENT
import com.hapley.pocketqr.ui.settings.SettingsFragment
import com.hapley.pocketqr.ui.settings.SortMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.Comparator

@HiltViewModel
class BarcodeHistoryViewModel @Inject constructor(private val barcodeUseCase: BarcodeUseCase, private val appPreferences: AppPreferences, private val tracker: Tracker) : ViewModel() {

    var showTutorial: Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, true)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_HISTORY_SHOW_TUTORIAL, value) }
        }

    @SortMode
    var sortMode: String
        get() = appPreferences.settings.getString(SettingsFragment.BARCODE_HISTORY_SORT, RECENT) ?: RECENT
        private set(value) {
            appPreferences.settings.edit { putString(SettingsFragment.BARCODE_HISTORY_SORT, value) }
            tracker.sort(value)
        }

    fun updateSortMode(@SortMode sortMode: String) {
        if (this.sortMode != sortMode) {
            this.sortMode = sortMode
            calculate()
        }
    }

    private fun sortModeToComparator(@SortMode sortMode: String): Comparator<Barcode> {
        val alphabetComparatorAscending: Comparator<Barcode> by lazy {
            Comparator<Barcode> { lhs, rhs -> lhs.label.compareTo(rhs.label) }
        }

        val favoriteComparator: Comparator<Barcode> by lazy {
            Comparator<Barcode> { lhs, rhs -> rhs.isFavorite.compareTo(lhs.isFavorite) }
        }

        val clickCountComparator: Comparator<Barcode> by lazy {
            Comparator<Barcode> { lhs, rhs -> rhs.clickCount.compareTo(lhs.clickCount) }
        }

        val scannedDateComparator: Comparator<Barcode> by lazy {
            Comparator<Barcode> { lhs, rhs -> rhs.created.compareTo(lhs.created) }
        }

        fun mergeComparator(comparator: Comparator<Barcode>): Comparator<Barcode> {
            return Comparator<Barcode> { lhs, rhs ->
                val favoriteCompare = favoriteComparator.compare(lhs, rhs)
                if (favoriteCompare == 0) {
                    comparator.compare(lhs, rhs)
                } else favoriteCompare
            }
        }

        return when (sortMode) {
            RECENT -> mergeComparator(scannedDateComparator)
            MOST_FREQUENT -> mergeComparator(clickCountComparator)
            ALPHABETICAL -> mergeComparator(alphabetComparatorAscending)
            else -> mergeComparator(scannedDateComparator)
        }
    }

    fun calculate(searchQuery: String = "") {
        viewModelScope.launch {
            barcodeUseCase.getAllFlowSorted(sortModeToComparator(sortMode))
                .map { barcodeList -> barcodeList.map { BarcodeItem(it) } }
                .collect {
                    val result = if (searchQuery.isBlank()) it
                    else it.filter { barcodeItem ->
                        barcodeItem.title.toLowerCase(Locale.getDefault())
                            .contains(searchQuery.toLowerCase(Locale.getDefault()))
                    }
                    _barcodeListLiveData.postValue(result)
                }
        }
    }

    private val _barcodeListLiveData = MutableLiveData<List<BarcodeItem>>()

    val barcodeListLiveData: LiveData<List<BarcodeItem>> = _barcodeListLiveData

    lateinit var selectedItemWithPosition: Triple<View, BarcodeItem, Int>

    fun updateFavoriteFlag() {
        viewModelScope.launch {
            val isFavorite = selectedItemWithPosition.second.isFavorite.not()
            barcodeUseCase.updateFavorite(selectedItemWithPosition.second, isFavorite)
        }
    }

    fun deleteBarcode(barcodeItem: BarcodeItem) {
        viewModelScope.launch {
            barcodeUseCase.deleteBarcode(barcodeItem)
        }
    }
}
