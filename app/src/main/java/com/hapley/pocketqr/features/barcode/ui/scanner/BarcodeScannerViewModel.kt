package com.hapley.pocketqr.features.barcode.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeType
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.domain.CONTACT
import com.hapley.pocketqr.features.barcode.domain.EMAIL
import com.hapley.pocketqr.features.barcode.domain.GEO
import com.hapley.pocketqr.features.barcode.domain.ISBN
import com.hapley.pocketqr.features.barcode.domain.PHONE
import com.hapley.pocketqr.features.barcode.domain.SMS
import com.hapley.pocketqr.features.barcode.domain.UNKNOWN
import com.hapley.pocketqr.features.barcode.domain.URL
import com.hapley.pocketqr.features.barcode.domain.WIFI
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
            tempRawValue = mlKitBarcode.rawValue.toString()

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
                format = mlKitBarcode.format,
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
