package com.nandoo.pocketqr.features.barcode.data

import com.nandoo.pocketqr.db.BarcodeDao

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */
class BarcodeRepository(private val barcodeDao: BarcodeDao) {

    fun getAllLiveData() = barcodeDao.getAllLiveData()

    fun getById(id: Int) = barcodeDao.getById(id)

    fun getByIdLiveData(id: Int) = barcodeDao.getByIdLiveData(id)

    fun getLastId() = barcodeDao.getLastId()

    suspend fun insert(barcodeEntity: BarcodeEntity) = barcodeDao.insertData(barcodeEntity)

    fun updateLabel(label: String, id: Int) = barcodeDao.updateLabel(label, id)
}
