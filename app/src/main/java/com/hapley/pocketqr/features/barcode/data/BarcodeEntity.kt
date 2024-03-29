package com.hapley.pocketqr.features.barcode.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hapley.pocketqr.features.barcode.domain.BarcodeFormat
import com.hapley.pocketqr.features.barcode.domain.BarcodeType

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

@Entity
data class BarcodeEntity(
    val rawValue: String,
    val label: String,
    val displayValue: String,
    val created: Long,

    @BarcodeFormat
    val format: Int,

    @BarcodeType
    val type: Int,

    val isFavorite: Boolean,
    val clickCount: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
