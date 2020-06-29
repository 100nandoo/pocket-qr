package com.nandoo.pocketqr.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nandoo.pocketqr.common.BaseDao
import com.nandoo.pocketqr.features.barcode.data.BarcodeEntity

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

@Dao
interface BarcodeDao : BaseDao<BarcodeEntity> {

    @Query("SELECT * from barcodeentity")
    fun getAll(): List<BarcodeEntity>

    @Query("SELECT * from barcodeentity")
    fun getAllLiveData(): LiveData<List<BarcodeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM barcodeentity WHERE rawValue = :rawValue LIMIT 1)")
    suspend fun isExist(rawValue: String): Boolean

    @Transaction
    suspend fun insertData(barcodeEntity: BarcodeEntity) {
        val isExist = isExist(barcodeEntity.rawValue)
        if (isExist.not()) {
            insert(barcodeEntity)
        }
    }
}
