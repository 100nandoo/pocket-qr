package com.nandoo.pocketqr.features.barcode.ui.history

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import com.nandoo.pocketqr.features.barcode.ui.BarcodeItem

class BarcodeHistoryViewModel(barcodeUseCase: BarcodeUseCase) : ViewModel() {

    var openBarcodeHistoryFirst = false

    val barcodesLiveData = Transformations.map(barcodeUseCase.getAllLiveData()) { barcodes -> barcodes.map { BarcodeItem(it) } }
}
