package com.hapley.pocketqr.features.barcode.domain

import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.data.BarcodeEntity
import com.hapley.pocketqr.features.barcode.data.BarcodeRepository
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.preview.ui.PreviewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

class BarcodeUseCase @Inject constructor(private val barcodeRepository: BarcodeRepository, private val tracker: Tracker) {

    fun getAllFlow(): Flow<List<Barcode>> {
        return barcodeRepository.getAllFlow().map { barcodeEntityList ->
            barcodeEntityList.map { Barcode(it) }
        }
    }

    fun getAllFlowSorted(comparator: Comparator<Barcode>): Flow<List<Barcode>> {
        return barcodeRepository.getAllFlow().map { barcodeEntityList ->
            barcodeEntityList.map { Barcode(it) }.sortedWith(comparator)
        }
    }

    fun getStarredFlow(): Flow<List<Barcode>> {
        return barcodeRepository.getStarredFlow().map { barcodeEntityList ->
            barcodeEntityList.map { barcodeEntity ->
                Barcode(barcodeEntity)
            }
        }
    }

    fun getById(id: Int): Barcode {
        return Barcode(barcodeRepository.getById(id))
    }

    fun getByIdFlow(id: Int): Flow<Barcode> = barcodeRepository.getByIdFlow(id).map { Barcode(it) }

    fun getPreviewById(id: Int): PreviewItem {
        val barcodeEntity = barcodeRepository.getById(id)
        return PreviewItem(barcodeEntity.label, barcodeEntity.rawValue)
    }

    fun getPreviewByIdFlow(id: Int): Flow<PreviewItem> {
        return barcodeRepository.getByIdFlow(id).map { entity -> PreviewItem(entity.label, entity.rawValue) }
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

    suspend fun updateBarcodes(vararg barcode: Barcode) {
        barcodeRepository.updateBarcodes(*barcode.map { it.toEntity }.toTypedArray())
    }

    suspend fun deleteBarcode(barcodeItem: BarcodeItem) {
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
