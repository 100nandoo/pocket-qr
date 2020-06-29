package com.nandoo.pocketqr.features.barcode.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nandoo.pocketqr.features.barcode.data.BarcodeEntity
import com.nandoo.pocketqr.features.barcode.data.BarcodeRepository
import java.util.*
import com.google.mlkit.vision.barcode.Barcode as MLKitBarcode

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

class BarcodeUseCase constructor(private val barcodeRepository: BarcodeRepository) {

    fun getAllLiveData(): LiveData<List<Barcode>> {
        return Transformations.map(barcodeRepository.getAllLiveData()) { barcodeEntities ->
            barcodeEntities.map { barcodeEntity ->
                Barcode(barcodeEntity)
            }
        }
    }

    suspend fun insert(mlKitBarcode: MLKitBarcode) {
        mlKitBarcode.map()?.let { barcodeRepository.insert(it) }
    }
}

fun MLKitBarcode.map(): BarcodeEntity? {
    this.rawValue?.let {
        return BarcodeEntity(
            rawValue = it,
            created = Date().time,
            format = this.format,
            type = this.valueType
        )
    }
    return null
}
