package com.nandoo.pocketqr.features.barcode.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import kotlinx.coroutines.launch
import com.google.mlkit.vision.barcode.Barcode as MlKitBarcode

class BarcodeScannerViewModel constructor(private val barcodeUseCase: BarcodeUseCase) : ViewModel() {

    var openBarcodeHistoryFirst = false

    var tempRawValue: String? = null

    fun setBarcode(mlKitBarcode: MlKitBarcode) {
        tempRawValue = mlKitBarcode.rawValue.toString()
        viewModelScope.launch {
            barcodeUseCase.insert(mlKitBarcode)
        }
    }
}
