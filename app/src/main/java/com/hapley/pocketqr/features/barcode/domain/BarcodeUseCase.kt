package com.hapley.pocketqr.features.barcode.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hapley.pocketqr.features.barcode.data.BarcodeEntity
import com.hapley.pocketqr.features.barcode.data.BarcodeRepository

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

    fun getById(id: Int): Barcode {
        return Barcode(barcodeRepository.getById(id))
    }

    fun getByIdLiveData(id: Int): LiveData<Barcode> {
        return Transformations.map(barcodeRepository.getByIdLiveData(id)) { entity ->
            Barcode(entity)
        }
    }

    fun getLastId(): Int {
        return barcodeRepository.getLastId()
    }

    suspend fun insert(barcode: Barcode) {
        barcodeRepository.insert(barcode.toEntity)
    }

    suspend fun updateLabel(label: String, id: Int) {
        barcodeRepository.updateLabel(label, id)
    }

    suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        barcodeRepository.updateFavorite(id, isFavorite)
    }

    suspend fun updateBarcodes(vararg barcode: Barcode){
        barcodeRepository.updateBarcodes(*barcode.map { it.toEntity }.toTypedArray())
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
            type = this.type.value,
            isFavorite = this.isFavorite
        )