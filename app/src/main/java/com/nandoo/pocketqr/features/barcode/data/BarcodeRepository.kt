package com.nandoo.pocketqr.features.barcode.data

import com.nandoo.pocketqr.db.BarcodeDao

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */
class BarcodeRepository(private val barcodeDao: BarcodeDao) {

    fun getAllLiveData() = barcodeDao.getAllLiveData()

    suspend fun insert(barcodeEntity: BarcodeEntity) = barcodeDao.insertData(barcodeEntity)
}
