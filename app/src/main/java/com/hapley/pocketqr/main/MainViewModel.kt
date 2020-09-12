package com.hapley.pocketqr.main

import androidx.lifecycle.*
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import kotlinx.coroutines.launch

class MainViewModel(private val barcodeUseCase: BarcodeUseCase): ViewModel() {

    val starredBarcodesLiveData = Transformations.map(barcodeUseCase.getStarredLiveData()) { barcodes -> barcodes.map { BarcodeItem(it) } }

    private val _barcodeItemLiveData: MutableLiveData<BarcodeItem> by lazy { MutableLiveData<BarcodeItem>() }

    fun barcodeItemLiveData(): LiveData<BarcodeItem> = _barcodeItemLiveData

    fun actionOpenUrl(barcodeItem: BarcodeItem?){
        _barcodeItemLiveData.value = barcodeItem
    }

    fun incrementClickCount(barcodeId: Int){
        viewModelScope.launch {
            barcodeUseCase.incrementClickCount(barcodeId)
        }
    }
}