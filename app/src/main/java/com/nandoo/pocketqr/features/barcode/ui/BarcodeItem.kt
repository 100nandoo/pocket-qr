package com.nandoo.pocketqr.features.barcode.ui

import android.view.View
import androidx.annotation.DrawableRes
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.features.barcode.domain.Barcode
import com.nandoo.pocketqr.features.barcode.domain.BarcodeType
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
    val rawValue: String
) : AbstractItem<BarcodeItem.ViewHolder>() {

    constructor(barcode: Barcode) : this(
        barcode.id.toLong(),
        if (barcode.label.isBlank()) barcode.displayValue else barcode.label,
        barcode.displayValue,
        barcode.type.getIcon(),
        Date(barcode.created),
        barcode.rawValue
    )

    class ViewHolder(view: View) : FastAdapter.ViewHolder<BarcodeItem>(view) {

        override fun bindView(item: BarcodeItem, payloads: List<Any>) {
            itemView.iv_icon.setImageResource(item.icon)
            itemView.tv_title.text = item.title
            itemView.tv_subtitle.text = item.subtitle
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
