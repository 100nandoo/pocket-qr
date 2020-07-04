package com.nandoo.pocketqr.features.barcode.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nandoo.pocketqr.features.barcode.domain.Barcode
import com.nandoo.pocketqr.features.barcode.domain.BarcodeType
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import com.google.mlkit.vision.barcode.Barcode as MlKitBarcode

class BarcodeScannerViewModel constructor(private val barcodeUseCase: BarcodeUseCase) : ViewModel() {

    var openBarcodeHistoryFirst = false

    var tempRawValue: String? = null

    fun setBarcode(mlKitBarcode: MlKitBarcode) {
        viewModelScope.launch(Dispatchers.IO) {
            tempRawValue = mlKitBarcode.rawValue.toString()

            val availableId = barcodeUseCase.getLastId() + 1

            mlKitBarcode.toDomain(availableId)?.let { barcodeUseCase.insert(it) }
        }
    }
}

fun MlKitBarcode.toDomain(id: Int): Barcode? {
    return rawValue?.let {
        Barcode(
            id = id,
            rawValue = it,
            label = "",
            displayValue = displayValue ?: "",
            created = Date().time,
            format = format,
            type = this.generateType()
        )
    }
}

fun MlKitBarcode.generateType(): BarcodeType = when (valueType) {
    MlKitBarcode.TYPE_EMAIL -> BarcodeType.EMAIL
    MlKitBarcode.TYPE_ISBN -> BarcodeType.ISBN
    MlKitBarcode.TYPE_PHONE -> BarcodeType.PHONE
    MlKitBarcode.TYPE_URL -> BarcodeType.URL
    else -> BarcodeType.UNKNOWN
}