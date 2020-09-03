package com.hapley.pocketqr.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hapley.pocketqr.common.BaseDao
import com.hapley.pocketqr.features.barcode.data.BarcodeEntity

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

@Dao
interface BarcodeDao : BaseDao<BarcodeEntity> {

    @Query("SELECT * from barcodeentity")
    fun getAll(): List<BarcodeEntity>

    @Query("SELECT * from barcodeentity")
    fun getAllLiveData(): LiveData<List<BarcodeEntity>>

    @Query("SELECT * FROM barcodeentity WHERE ID=:id")
    fun getById(id: Int): BarcodeEntity

    @Query("SELECT * FROM barcodeentity WHERE ID=:id")
    fun getByIdLiveData(id: Int): LiveData<BarcodeEntity>

    @Query("SELECT * FROM barcodeentity ORDER BY ID DESC LIMIT 1")
    fun getLastId(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM barcodeentity WHERE rawValue = :rawValue LIMIT 1)")
    suspend fun isExist(rawValue: String): Boolean

    @Insert
    suspend fun insertData(barcodeEntity: BarcodeEntity) {
        val isExist = isExist(barcodeEntity.rawValue)
        if (isExist.not()) {
            insert(barcodeEntity)
        }
    }

    @Update
    suspend fun updateBarcodes(vararg barcodes: BarcodeEntity)

    @Query("UPDATE barcodeentity SET label = :label WHERE id = :id")
    suspend fun updateLabel(label: String, id: Int)

    @Query("UPDATE barcodeentity SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE barcodeentity SET clickCount = clickCount + 1 WHERE id = :id")
    suspend fun incrementClickCount(id: Int)

    @Query("DELETE FROM barcodeentity WHERE id = :id")
    suspend fun delete(id: Int)
}
