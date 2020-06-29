package com.nandoo.pocketqr.features.barcode.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import kotlinx.coroutines.launch

class BarcodeScannerViewModel constructor(private val barcodeUseCase: BarcodeUseCase) : ViewModel() {

    var openBarcodeHistoryFirst = false

    var tempRawValue: String? = null

    fun setBarcode(firebaseBarcode: FirebaseVisionBarcode) {
        tempRawValue = firebaseBarcode.rawValue.toString()
        viewModelScope.launch {
            barcodeUseCase.insert(firebaseBarcode)
        }
    }
}
