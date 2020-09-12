package com.hapley.pocketqr.main

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem

class MainViewModel(private val barcodeUseCase: BarcodeUseCase): ViewModel() {

    val starredBarcodesLiveData = Transformations.map(barcodeUseCase.getStarredLiveData()) { barcodes -> barcodes.map { BarcodeItem(it) } }

}