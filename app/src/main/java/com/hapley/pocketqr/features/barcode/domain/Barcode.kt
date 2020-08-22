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
    val type: BarcodeType,
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
        type = barcodeEntity.generateType(),
        isFavorite = barcodeEntity.isFavorite,
        clickCount = barcodeEntity.clickCount
    )
}

enum class BarcodeType(val value: Int) {
    CONTACT(MLKitBarcode.TYPE_CONTACT_INFO),
    EMAIL(MLKitBarcode.TYPE_EMAIL),
    GEO(MLKitBarcode.TYPE_GEO),
    ISBN(MLKitBarcode.TYPE_ISBN),
    PHONE(MLKitBarcode.TYPE_PHONE),
    SMS(MLKitBarcode.TYPE_SMS),
    URL(MLKitBarcode.TYPE_URL),
    WIFI(MLKitBarcode.TYPE_WIFI),
    UNKNOWN(-1)
}

fun BarcodeEntity.generateType(): BarcodeType = when (type) {
    MLKitBarcode.TYPE_CONTACT_INFO -> BarcodeType.CONTACT
    MLKitBarcode.TYPE_EMAIL -> BarcodeType.EMAIL
    MLKitBarcode.TYPE_GEO -> BarcodeType.GEO
    MLKitBarcode.TYPE_ISBN -> BarcodeType.ISBN
    MLKitBarcode.TYPE_PHONE -> BarcodeType.PHONE
    MLKitBarcode.TYPE_SMS -> BarcodeType.SMS
    MLKitBarcode.TYPE_URL -> BarcodeType.URL
    MLKitBarcode.TYPE_WIFI -> BarcodeType.WIFI
    else -> BarcodeType.UNKNOWN
}