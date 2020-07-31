package com.hapley.pocketqr.features.barcode.ui

import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_ABBREV_ALL
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.hapley.pocketqr.R
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeType
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils
import kotlinx.android.synthetic.main.barcode_list_item.view.*
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
    val created: Date,
    val rawValue: String,
    var isFavorite: Boolean
) : AbstractItem<BarcodeItem.ViewHolder>() {

    constructor(barcode: Barcode) : this(
        barcode.id.toLong(),
        if (barcode.label.isBlank()) barcode.displayValue else barcode.label,
        barcode.displayValue,
        barcode.type.getIcon(),
        Date(barcode.created),
        barcode.rawValue,
        barcode.isFavorite
    )

    class ViewHolder(view: View) : FastAdapter.ViewHolder<BarcodeItem>(view) {

        override fun bindView(item: BarcodeItem, payloads: List<Any>) {
            val background = if (layoutPosition % 2 == 0) R.drawable.background_light_grey_effect else R.drawable.background_white_effect
            itemView.background = FastAdapterUIUtils.getSelectableBackground(itemView.context, ContextCompat.getColor(itemView.context, R.color.primaryLightColor), true)
            itemView.iv_icon.setImageResource(item.icon)
            itemView.tv_title.text = item.title
            itemView.tv_created_at.text = DateUtils.formatDateTime(itemView.context, item.created.time, FORMAT_ABBREV_ALL)
            itemView.tv_subtitle.text = item.subtitle
            itemView.iv_favorite.isInvisible = item.isFavorite.not()
        }

        override fun unbindView(item: BarcodeItem) = Unit
    }

    override var identifier: Long
        get() = id
        set(_) {}

    override val layoutRes: Int
        get() = R.layout.barcode_list_item

    override val type: Int
        get() = BarcodeItemView.BARCODE.id

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }
}

fun BarcodeType.getIcon(): Int {
    return when (this) {
        BarcodeType.EMAIL -> R.drawable.ic_barcode_mail
        BarcodeType.ISBN -> R.drawable.ic_barcode_isbn
        BarcodeType.PHONE -> R.drawable.ic_barcode_phone
        BarcodeType.URL -> R.drawable.ic_barcode_url
        else -> R.drawable.ic_barcode_other
    }
}
