package com.hapley.pocketqr.features.barcode.data

import com.hapley.pocketqr.db.BarcodeDao
import javax.inject.Inject

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */
class BarcodeRepository @Inject constructor(private val barcodeDao: BarcodeDao) {

    fun getAllFlow() = barcodeDao.getAllFlow()

    fun getStarredFlow() = barcodeDao.getStarredFlow()

    fun getById(id: Int) = barcodeDao.getById(id)

    fun getByIdFlow(id: Int) = barcodeDao.getByIdFlow(id)

    fun getLastId() = barcodeDao.getLastId()

    suspend fun insert(barcodeEntity: BarcodeEntity) {
        val isExist = barcodeDao.isExist(barcodeEntity.rawValue)
        if (isExist.not()) {
            barcodeDao.insertData(barcodeEntity)
        }
    }

    suspend fun updateLabel(label: String, id: Int) = barcodeDao.updateLabel(label, id)

    suspend fun updateFavorite(id: Int, isFavorite: Boolean) = barcodeDao.updateFavorite(id, isFavorite)

    suspend fun incrementClickCount(id: Int) = barcodeDao.incrementClickCount(id)

    suspend fun updateBarcodes(vararg barcodeEntity: BarcodeEntity) = barcodeDao.updateBarcodes(*barcodeEntity)

    suspend fun deleteBarcode(id: Int) = barcodeDao.delete(id)
}
