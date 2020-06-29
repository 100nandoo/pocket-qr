package com.nandoo.pocketqr.features.barcode.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.features.barcode.domain.Barcode
import com.nandoo.pocketqr.features.barcode.domain.BarcodeType

/**
 * Created by Fernando Fransisco Halim on 2020-01-28.
 */
enum class BarcodeItemView(val id: Int) {
    BARCODE(1),
    UNKNOWN(0)
}

open class BarcodeItem(val barcode: Barcode) : AbstractItem<BarcodeItem.ViewHolder>() {

    class ViewHolder(view: View) : FastAdapter.ViewHolder<BarcodeItem>(view) {
        private val ivIcon = view.findViewById<ImageView>(R.id.iv_icon)
        private val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        private val tvSubtitle = view.findViewById<TextView>(R.id.tv_subtitle)

        override fun bindView(item: BarcodeItem, payloads: List<Any>) {
            ivIcon.setImageResource(item.barcode.type.getIcon())
            tvTitle.text = item.barcode.title
            tvSubtitle.text = item.barcode.rawValue
        }

        override fun unbindView(item: BarcodeItem) {
            tvTitle.text = null
            tvSubtitle.text = null
        }
    }

    override var identifier: Long
        get() = barcode.id.toLong()
        set(value) {}

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
