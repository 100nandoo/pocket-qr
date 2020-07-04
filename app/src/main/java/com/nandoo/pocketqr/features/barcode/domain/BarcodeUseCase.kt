package com.nandoo.pocketqr.features.barcode.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nandoo.pocketqr.features.barcode.data.BarcodeEntity
import com.nandoo.pocketqr.features.barcode.data.BarcodeRepository

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

    fun getLastId(): Int {
        return barcodeRepository.getLastId()
    }

    suspend fun insert(barcode: Barcode) {
        barcodeRepository.insert(barcode.toEntity)
    }
}

private val Barcode.toEntity: BarcodeEntity
    get() =
        BarcodeEntity(
            rawValue = this.rawValue,
            label = this.label,
            displayValue = this.displayValue,
            created = this.created,
            format = this.format,
            type = this.type.value
        )