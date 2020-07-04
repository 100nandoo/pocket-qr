package com.nandoo.pocketqr.features.barcode.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import com.nandoo.pocketqr.features.barcode.ui.BarcodeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarcodeDetailViewModel(val barcodeUseCase: BarcodeUseCase) : ViewModel() {

    var id = -1

    val barcodeLiveData: LiveData<BarcodeItem> by lazy {
        Transformations.map(barcodeUseCase.getByIdLiveData(id)) { barcode -> BarcodeItem(barcode) }
    }

    fun submit(label: String) {
        if(id > -1){
            viewModelScope.launch(Dispatchers.IO) {
                barcodeLiveData.value?.title = label
                barcodeUseCase.updateLabel(label, id)
            }
        }
    }
}