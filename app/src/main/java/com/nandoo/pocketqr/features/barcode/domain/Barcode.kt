package com.nandoo.pocketqr.features.barcode.domain

import com.nandoo.pocketqr.common.extension.rawValueToUri
import com.nandoo.pocketqr.features.barcode.data.BarcodeEntity
import java.util.*

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

data class Barcode(val id: Int, val rawValue: String, val type: BarcodeType, val title: String, val created: Date) {

    constructor(barcodeEntity: BarcodeEntity) : this(
        id = barcodeEntity.id,
        rawValue = barcodeEntity.rawValue,
        type = barcodeEntity.generateType(),
        title = barcodeEntity.generateTitle(),
        created = Date(barcodeEntity.created)
    )
}

enum class BarcodeType {
    URL, EMAIL, PHONE, ISBN, UNKNOWN
}

fun BarcodeEntity.generateType(): BarcodeType = when (type) {
    2 -> BarcodeType.EMAIL
    3 -> BarcodeType.ISBN
    4 -> BarcodeType.PHONE
    8 -> BarcodeType.URL
    else -> BarcodeType.UNKNOWN
}

private fun BarcodeEntity.generateTitle(): String = when (this.generateType()) {
    BarcodeType.EMAIL -> this.rawValue.rawValueToUri()?.path ?: this.rawValue
    BarcodeType.ISBN -> this.rawValue
    BarcodeType.PHONE -> this.rawValue.rawValueToUri()?.path ?: this.rawValue
    BarcodeType.URL -> this.rawValue.rawValueToUri()?.authority ?: this.rawValue
    else -> rawValue
}
