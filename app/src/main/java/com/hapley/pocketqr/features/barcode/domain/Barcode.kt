package com.hapley.pocketqr.features.barcode.domain

import com.hapley.pocketqr.features.barcode.data.BarcodeEntity
import com.google.mlkit.vision.barcode.Barcode as MLKitBarcode

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
    val type: BarcodeType
) {

    constructor(barcodeEntity: BarcodeEntity) : this(
        id = barcodeEntity.id,
        rawValue = barcodeEntity.rawValue,
        label = barcodeEntity.label,
        displayValue = barcodeEntity.displayValue,
        created = barcodeEntity.created,
        format = barcodeEntity.format,
        type = barcodeEntity.generateType()
    )
}

enum class BarcodeType(val value: Int) {
    URL(MLKitBarcode.TYPE_URL), EMAIL(MLKitBarcode.TYPE_EMAIL), PHONE(MLKitBarcode.TYPE_PHONE), ISBN(MLKitBarcode.TYPE_ISBN), UNKNOWN(-1)
}

fun BarcodeEntity.generateType(): BarcodeType = when (type) {
    MLKitBarcode.TYPE_EMAIL -> BarcodeType.EMAIL
    MLKitBarcode.TYPE_ISBN -> BarcodeType.ISBN
    MLKitBarcode.TYPE_PHONE -> BarcodeType.PHONE
    MLKitBarcode.TYPE_URL -> BarcodeType.URL
    else -> BarcodeType.UNKNOWN
}