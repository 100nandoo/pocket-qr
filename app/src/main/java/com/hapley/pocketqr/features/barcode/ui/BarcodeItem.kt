package com.hapley.pocketqr.features.barcode.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils.FORMAT_ABBREV_ALL
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.getRelativeTimeSpanString
import androidx.annotation.DrawableRes
import androidx.core.view.isInvisible
import com.hapley.core.ui.helper.ViewBindingKotlinModel
import com.hapley.pocketqr.R
import com.hapley.pocketqr.databinding.BarcodeHistoryItemBinding
import com.hapley.pocketqr.features.barcode.domain.AZTEC
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeFormat
import com.hapley.pocketqr.features.barcode.domain.BarcodeType
import com.hapley.pocketqr.features.barcode.domain.CODEBAR
import com.hapley.pocketqr.features.barcode.domain.CODE_128
import com.hapley.pocketqr.features.barcode.domain.CODE_39
import com.hapley.pocketqr.features.barcode.domain.CODE_93
import com.hapley.pocketqr.features.barcode.domain.CONTACT
import com.hapley.pocketqr.features.barcode.domain.DATA_MATRIX
import com.hapley.pocketqr.features.barcode.domain.EAN_13
import com.hapley.pocketqr.features.barcode.domain.EAN_8
import com.hapley.pocketqr.features.barcode.domain.EMAIL
import com.hapley.pocketqr.features.barcode.domain.GEO
import com.hapley.pocketqr.features.barcode.domain.ISBN
import com.hapley.pocketqr.features.barcode.domain.ITF
import com.hapley.pocketqr.features.barcode.domain.PDF_417
import com.hapley.pocketqr.features.barcode.domain.PHONE
import com.hapley.pocketqr.features.barcode.domain.QR_CODE
import com.hapley.pocketqr.features.barcode.domain.SMS
import com.hapley.pocketqr.features.barcode.domain.UPC_A
import com.hapley.pocketqr.features.barcode.domain.UPC_E
import com.hapley.pocketqr.features.barcode.domain.URL
import com.hapley.pocketqr.features.barcode.domain.WIFI
import com.hapley.pocketqr.main.MainActivity
import java.util.Date
import com.google.zxing.BarcodeFormat as ZxingFormat

/**
 * Created by Fernando Fransisco Halim on 2020-01-28.
 */
enum class BarcodeItemView(val id: Int) {
    BARCODE(1),
    UNKNOWN(0)
}

interface BarcodeItemListener {
    fun clickListener(id: Int)
}

data class BarcodeItem(
    val id: Long,
    var title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    @BarcodeType val barcodeType: Int,
    @BarcodeFormat val barcodeFormat: Int,
    val zxingFormat: ZxingFormat,
    val ratio: Pair<Float, Float>,
    val created: Date,
    val rawValue: String,
    val isFavorite: Boolean,
    val clickCount: Int
) : ViewBindingKotlinModel<BarcodeHistoryItemBinding>(R.layout.barcode_history_item) {

    companion object {
        fun getIcon(@BarcodeType type: Int): Int {
            return when (type) {
                CONTACT -> R.drawable.ic_barcode_type_contact
                EMAIL -> R.drawable.ic_barcode_type_mail
                GEO -> R.drawable.ic_barcode_type_geo
                ISBN -> R.drawable.ic_barcode_type_isbn
                PHONE -> R.drawable.ic_barcode_type_phone
                SMS -> R.drawable.ic_barcode_type_sms
                URL -> R.drawable.ic_barcode_type_url
                WIFI -> R.drawable.ic_barcode_type_wifi
                else -> R.drawable.ic_barcode_type_other
            }
        }

        fun toZxingFormat(@BarcodeFormat format: Int): ZxingFormat {
            return when (format) {
                CODE_128 -> ZxingFormat.CODE_128
                CODE_39 -> ZxingFormat.CODE_39
                CODE_93 -> ZxingFormat.CODE_39
                CODEBAR -> ZxingFormat.CODABAR
                DATA_MATRIX -> ZxingFormat.DATA_MATRIX
                EAN_13 -> ZxingFormat.EAN_13
                EAN_8 -> ZxingFormat.EAN_8
                ITF -> ZxingFormat.ITF
                QR_CODE -> ZxingFormat.QR_CODE
                UPC_A -> ZxingFormat.UPC_A
                UPC_E -> ZxingFormat.UPC_E
                PDF_417 -> ZxingFormat.PDF_417
                AZTEC -> ZxingFormat.AZTEC
                else -> ZxingFormat.QR_CODE
            }
        }

        fun getRatio(@BarcodeFormat format: Int): Pair<Float, Float> {
            return when (format) {
                CODE_128 -> Pair(2f, 1f)
                CODE_39 -> Pair(2.5f, 1f)
                CODE_93 -> Pair(1.23f, 1f)
                CODEBAR -> Pair(1.7f, 1f)
                DATA_MATRIX -> Pair(1f, 1f)
                EAN_13 -> Pair(1.7f, 1f)
                EAN_8 -> Pair(1.7f, 1f)
                ITF -> Pair(2.72f, 1f)
                QR_CODE -> Pair(1f, 1f)
                UPC_A -> Pair(1.37f, 1f)
                UPC_E -> Pair(1.37f, 1f)
                PDF_417 -> Pair(2f, 1f)
                AZTEC -> Pair(1f, 1f)
                else -> Pair(1f, 1f)
            }

        }
    }

    lateinit var listener: BarcodeItemListener

    constructor(barcode: Barcode) : this(
        barcode.id.toLong(),
        barcode.label.ifBlank { barcode.displayValue },
        barcode.displayValue,
        getIcon(barcode.type),
        barcode.type,
        barcode.format,
        toZxingFormat(barcode.format),
        getRatio(barcode.format),
        Date(barcode.created),
        barcode.rawValue,
        barcode.isFavorite,
        clickCount = barcode.clickCount
    )

    override fun BarcodeHistoryItemBinding.bind() {
        ivIcon.setImageResource(icon)
        tvLabel.text = title
        tvCreatedAt.text = getRelativeTimeSpanString(created.time, Date().time, MINUTE_IN_MILLIS, FORMAT_ABBREV_ALL)
        tvSubtitle.text = subtitle
        ivFavorite.isInvisible = isFavorite.not()

        cardHistoryItem.setOnClickListener { listener.clickListener(id.toInt()) }
    }
}

fun BarcodeItem.toShortcutInfo(context: Context): ShortcutInfo? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        ShortcutInfo.Builder(context, this.id.toString())
            .setShortLabel(this.title)
            .setIcon(Icon.createWithResource(context, this.icon))
            .setIntent(
                Intent(context, MainActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
                    .setData(Uri.parse("hapley://detail/${this.id.toInt()}"))
            )
            .build()
    } else null
}