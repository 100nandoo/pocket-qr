package com.nandoo.pocketqr.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nandoo.pocketqr.features.barcode.data.BarcodeEntity

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

@Database(entities = [BarcodeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao
}
