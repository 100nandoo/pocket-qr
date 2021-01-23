package com.hapley.pocketqr.features.barcode.ui.history.bottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ActionBottomSheetViewModel @Inject constructor(private val barcodeUseCase: BarcodeUseCase) : ViewModel() {

    var id = -1

    val barcodeLiveData: LiveData<BarcodeItem> by lazy {
        Transformations.map(barcodeUseCase.getByIdLiveData(id)) { barcode -> BarcodeItem(barcode) }
    }

    fun updateFavoriteFlag() {
        viewModelScope.launch {
            val barcodeItem = barcodeLiveData.value ?: return@launch
            val isFavorite = barcodeLiveData.value?.isFavorite?.not() ?: return@launch
            barcodeUseCase.updateFavorite(barcodeItem, isFavorite)
        }
    }

    fun submit(label: String) {
        if (id > -1) {
            viewModelScope.launch(Dispatchers.IO) {
                barcodeLiveData.value?.title = label
                barcodeUseCase.updateLabel(label, id)
                Timber.v("Finish!")
            }
        }
    }
}