package com.hapley.pocketqr.features.barcode.domain

import androidx.annotation.IntDef
import com.hapley.pocketqr.features.barcode.data.BarcodeEntity

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

data class Barcode(
    val id: Int,
    val rawValue: String,
    val label: String,
    val displayValue: String,
    val created: Long,
    val format: Int,

    @BarcodeType
    val type: Int,
    val isFavorite: Boolean,
    val clickCount: Int
) {

    constructor(barcodeEntity: BarcodeEntity) : this(
        id = barcodeEntity.id,
        rawValue = barcodeEntity.rawValue,
        label = barcodeEntity.label,
        displayValue = barcodeEntity.displayValue,
        created = barcodeEntity.created,
        format = barcodeEntity.format,
        type = barcodeEntity.type,
        isFavorite = barcodeEntity.isFavorite,
        clickCount = barcodeEntity.clickCount
    )
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [CONTACT, EMAIL, GEO, ISBN, PHONE, SMS, URL, WIFI, UNKNOWN], open = false)
annotation class BarcodeType

const val CONTACT = 1
const val EMAIL = 2
const val GEO = 10
const val ISBN = 3
const val PHONE = 4
const val SMS = 6
const val URL = 8
const val WIFI = 9
const val UNKNOWN = -1