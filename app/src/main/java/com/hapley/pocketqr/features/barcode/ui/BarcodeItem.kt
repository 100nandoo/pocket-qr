package com.hapley.pocketqr.features.barcode.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isInvisible
import com.hapley.pocketqr.R
import com.hapley.pocketqr.databinding.BarcodeHistoryItemBinding
import com.hapley.pocketqr.features.barcode.domain.*
import com.hapley.pocketqr.main.MainActivity
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.IDrawerSwipeableViewHolder
import com.mikepenz.fastadapter.swipe.ISwipeable
import kotlinx.android.synthetic.main.barcode_history_item.view.*
import java.util.*

/**
 * Created by Fernando Fransisco Halim on 2020-01-28.
 */
enum class BarcodeItemView(val id: Int) {
    BARCODE(1),
    UNKNOWN(0)
}

open class BarcodeItem(
    val id: Long,
    var title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    @BarcodeType val barcodeType: Int,
    val created: Date,
    val rawValue: String,
    val isFavorite: Boolean,
    val clickCount: Int
) : AbstractBindingItem<BarcodeHistoryItemBinding>(), ISwipeable {

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

    override fun bindView(binding: BarcodeHistoryItemBinding, payloads: List<Any>) {
        binding.ivIcon.setImageResource(icon)
        binding.tvLabel.text = title
        binding.tvCreatedAt.text = getRelativeTimeSpanString(created.time, Date().time, MINUTE_IN_MILLIS, FORMAT_ABBREV_ALL)
        binding.tvSubtitle.text = subtitle
        binding.ivFavorite.isInvisible = isFavorite.not()
    }

    override var identifier: Long
        get() = id
        set(_) {}

    override val type: Int
        get() = BarcodeItemView.BARCODE.id

    override val isSwipeable: Boolean = true

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): BarcodeHistoryItemBinding {
        return BarcodeHistoryItemBinding.inflate(inflater, parent, false)
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