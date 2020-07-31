package com.hapley.pocketqr.features.barcode.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

@Entity
data class BarcodeEntity(
    val rawValue: String,
    val label: String,
    val displayValue: String,
    val created: Long,
    val format: Int,
    val type: Int,
    val isFavorite: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
