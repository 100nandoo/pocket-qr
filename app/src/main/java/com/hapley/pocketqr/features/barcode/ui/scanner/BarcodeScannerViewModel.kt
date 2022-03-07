package com.hapley.pocketqr.features.barcode.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeFormat
import com.hapley.pocketqr.features.barcode.domain.BarcodeType
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.domain.*
import com.hapley.pocketqr.util.PocketQrUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import com.google.mlkit.vision.barcode.Barcode as MlKitBarcode

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(private val barcodeUseCase: BarcodeUseCase, private val pocketQrUtil: PocketQrUtil) : ViewModel() {

    var tempRawValue: String? = null

    fun insertBarcode(mlKitBarcode: MlKitBarcode) {
        viewModelScope.launch(Dispatchers.Main) {
            tempRawValue = mlKitBarcode.rawValue?.toString()

            viewModelScope.launch(Dispatchers.IO) {
                val availableId = barcodeUseCase.getLastId() + 1
                convertToDomain(mlKitBarcode, availableId)?.let { barcodeUseCase.insert(it) }
            }

        }
    }

    fun convertToDomain(mlKitBarcode: MlKitBarcode, id: Int): Barcode? {
        return mlKitBarcode.rawValue?.let {
            Barcode(
                id = id,
                rawValue = it,
                label = pocketQrUtil.extractSafeEntryLabel(it),
                displayValue = mlKitBarcode.displayValue ?: "",
                created = Date().time,
                format = mlKitBarcode.generateFormat(),
                type = mlKitBarcode.generateType(),
                isFavorite = false,
                clickCount = 0
            )
        }
    }
}

@BarcodeType
fun MlKitBarcode.generateType(): Int = when (valueType) {
    MlKitBarcode.TYPE_CONTACT_INFO -> CONTACT
    MlKitBarcode.TYPE_EMAIL -> EMAIL
    MlKitBarcode.TYPE_GEO -> GEO
    MlKitBarcode.TYPE_ISBN -> ISBN
    MlKitBarcode.TYPE_PHONE -> PHONE
    MlKitBarcode.TYPE_SMS -> SMS
    MlKitBarcode.TYPE_URL -> URL
    MlKitBarcode.TYPE_WIFI -> WIFI
    else -> UNKNOWN
}

@BarcodeFormat
fun MlKitBarcode.generateFormat(): Int = when (format) {
    MlKitBarcode.FORMAT_CODE_128 -> CODE_128
    MlKitBarcode.FORMAT_CODE_39 -> CODE_39
    MlKitBarcode.FORMAT_CODE_93 -> CODE_93
    MlKitBarcode.FORMAT_CODABAR -> CODEBAR
    MlKitBarcode.FORMAT_DATA_MATRIX -> DATA_MATRIX
    MlKitBarcode.FORMAT_EAN_13 -> EAN_13
    MlKitBarcode.FORMAT_EAN_8 -> EAN_8
    MlKitBarcode.FORMAT_ITF -> ITF
    MlKitBarcode.FORMAT_QR_CODE -> QR_CODE
    MlKitBarcode.FORMAT_UPC_A -> UPC_A
    MlKitBarcode.FORMAT_UPC_E -> UPC_E
    MlKitBarcode.FORMAT_PDF417 -> PDF_417
    MlKitBarcode.FORMAT_AZTEC -> AZTEC
    else -> FORMAT_UNKNOWN
}