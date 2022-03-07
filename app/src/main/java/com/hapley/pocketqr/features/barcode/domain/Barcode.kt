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
    @BarcodeFormat
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
const val UNKNOWN = 0


@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [CODE_128, CODE_39, CODE_93, CODEBAR, DATA_MATRIX, EAN_13, EAN_8, ITF, QR_CODE, UPC_A, UPC_E, PDF_417, AZTEC, FORMAT_UNKNOWN], open = false)
annotation class BarcodeFormat

const val CODE_128 = 1
const val CODE_39 = 2
const val CODE_93 = 4
const val CODEBAR = 8
const val DATA_MATRIX = 16
const val EAN_13 = 32
const val EAN_8 = 64
const val ITF = 128
const val QR_CODE = 256
const val UPC_A = 512
const val UPC_E = 1024
const val PDF_417 = 2048
const val AZTEC = 4096
const val FORMAT_UNKNOWN = -1

fun Int.getBarcodeTypeName(): String {
    return when (this) {
        CONTACT -> ::CONTACT.name
        EMAIL -> ::EMAIL.name
        GEO -> ::GEO.name
        ISBN -> ::ISBN.name
        PHONE -> ::PHONE.name
        SMS -> ::SMS.name
        URL -> ::URL.name
        WIFI -> ::WIFI.name
        else -> ::UNKNOWN.name
    }
}

fun Int.getBarcodeFormatName(): String {
    return when (this) {
        CODE_128 -> ::CODE_128.name
        CODE_39 -> ::CODE_39.name
        CODE_93 -> ::CODE_93.name
        CODEBAR -> ::CODEBAR.name
        DATA_MATRIX -> ::DATA_MATRIX.name
        EAN_13 -> ::EAN_13.name
        EAN_8 -> ::EAN_8.name
        ITF -> ::ITF.name
        QR_CODE -> ::QR_CODE.name
        UPC_A -> ::UPC_A.name
        UPC_E -> ::UPC_E.name
        PDF_417 -> ::PDF_417.name
        AZTEC -> ::AZTEC.name
        else -> ::FORMAT_UNKNOWN.name
    }
}