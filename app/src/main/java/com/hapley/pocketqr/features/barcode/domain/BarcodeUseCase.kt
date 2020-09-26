package com.hapley.pocketqr.features.barcode.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.data.BarcodeEntity
import com.hapley.pocketqr.features.barcode.data.BarcodeRepository
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

class BarcodeUseCase constructor(private val barcodeRepository: BarcodeRepository, private val tracker: Tracker) {

    fun getAllLiveData(): LiveData<List<Barcode>> {
        return Transformations.map(barcodeRepository.getAllLiveData()) { barcodeEntities ->
            barcodeEntities.map { barcodeEntity ->
                Barcode(barcodeEntity)
            }
        }
    }

    fun getStarredLiveData(): LiveData<List<Barcode>> {
        return Transformations.map(barcodeRepository.getStarredLiveData()) { barcodeEntities ->
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

    suspend fun updateFavorite(barcodeItem: BarcodeItem, isFavorite: Boolean) {
        barcodeRepository.updateFavorite(barcodeItem.id.toInt(), isFavorite)
        tracker.favorite(barcodeItem, isFavorite)
    }

    suspend fun incrementClickCount(id: Int) {
        barcodeRepository.incrementClickCount(id)
    }

    suspend fun updateBarcodes(vararg barcode: Barcode){
        barcodeRepository.updateBarcodes(*barcode.map { it.toEntity }.toTypedArray())
    }

    suspend fun deleteBarcode(barcodeItem: BarcodeItem){
        barcodeRepository.deleteBarcode(barcodeItem.id.toInt())
        tracker.delete(barcodeItem)
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
            type = this.type,
            isFavorite = this.isFavorite,
            clickCount = this.clickCount
        )