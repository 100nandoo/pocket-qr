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
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeType
import com.hapley.pocketqr.features.barcode.domain.CONTACT
import com.hapley.pocketqr.features.barcode.domain.EMAIL
import com.hapley.pocketqr.features.barcode.domain.GEO
import com.hapley.pocketqr.features.barcode.domain.ISBN
import com.hapley.pocketqr.features.barcode.domain.PHONE
import com.hapley.pocketqr.features.barcode.domain.SMS
import com.hapley.pocketqr.features.barcode.domain.URL
import com.hapley.pocketqr.features.barcode.domain.WIFI
import com.hapley.pocketqr.main.MainActivity
import java.util.Date

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
    val created: Date,
    val rawValue: String,
    val isFavorite: Boolean,
    val clickCount: Int
) : ViewBindingKotlinModel<BarcodeHistoryItemBinding>(R.layout.barcode_history_item) {

    lateinit var listener: BarcodeItemListener

    constructor(barcode: Barcode) : this(
        barcode.id.toLong(),
        if (barcode.label.isBlank()) barcode.displayValue else barcode.label,
        barcode.displayValue,
        barcode.type.getIcon(),
        barcode.type,
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

fun Int.getIcon(): Int {
    return when (this) {
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
