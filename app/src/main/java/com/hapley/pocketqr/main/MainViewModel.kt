package com.hapley.pocketqr.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val barcodeUseCase: BarcodeUseCase) : ViewModel() {

    val starredBarcodeListLiveData = barcodeUseCase.getStarredFlow().map { barcodeList -> barcodeList.map { BarcodeItem(it) } }.asLiveData()

    private val _barcodeItemLiveData: MutableLiveData<BarcodeItem> by lazy { MutableLiveData<BarcodeItem>() }

    fun barcodeItemLiveData(): LiveData<BarcodeItem> = _barcodeItemLiveData

    fun actionOpenUrl(barcodeItem: BarcodeItem?) {
        _barcodeItemLiveData.value = barcodeItem
    }

    fun incrementClickCount(barcodeId: Int) {
        viewModelScope.launch {
            barcodeUseCase.incrementClickCount(barcodeId)
        }
    }
}
