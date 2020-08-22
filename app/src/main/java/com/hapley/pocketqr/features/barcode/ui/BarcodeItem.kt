package com.hapley.pocketqr.features.barcode.ui

import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_ABBREV_ALL
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.view.isInvisible
import com.hapley.pocketqr.R
import com.hapley.pocketqr.features.barcode.domain.Barcode
import com.hapley.pocketqr.features.barcode.domain.BarcodeType
import com.mikepenz.fastadapter.FastAdapter
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
    val created: Date,
    val rawValue: String,
    val isFavorite: Boolean,
    val clickCount: Int
) : AbstractItem<BarcodeItem.ViewHolder>(), ISwipeable {

    constructor(barcode: Barcode) : this(
        barcode.id.toLong(),
        if (barcode.label.isBlank()) barcode.displayValue else barcode.label,
        barcode.displayValue,
        barcode.type.getIcon(),
        Date(barcode.created),
        barcode.rawValue,
        barcode.isFavorite,
        clickCount = barcode.clickCount
    )

    class ViewHolder(view: View) : FastAdapter.ViewHolder<BarcodeItem>(view), IDrawerSwipeableViewHolder {

        override fun bindView(item: BarcodeItem, payloads: List<Any>) {
            itemView.fl_history_item.transitionName = itemView.context.getString(R.string.barcode_history_transition_name, item.id)
            itemView.iv_icon.setImageResource(item.icon)
            itemView.tv_title.text = item.title
            itemView.tv_created_at.text = DateUtils.formatDateTime(itemView.context, item.created.time, FORMAT_ABBREV_ALL)
            itemView.tv_subtitle.text = item.subtitle
            itemView.iv_favorite.isInvisible = item.isFavorite.not()
        }

        override fun unbindView(item: BarcodeItem) = Unit

        override val swipeableView: View
            get() = itemView.cl_history_item
    }

    override var identifier: Long
        get() = id
        set(_) {}

    override val layoutRes: Int
        get() = R.layout.barcode_history_item

    override val type: Int
        get() = BarcodeItemView.BARCODE.id

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override val isSwipeable: Boolean = true
}

fun BarcodeType.getIcon(): Int {
    return when (this) {
        BarcodeType.CONTACT -> R.drawable.ic_barcode_type_contact
        BarcodeType.EMAIL -> R.drawable.ic_barcode_type_mail
        BarcodeType.GEO -> R.drawable.ic_barcode_type_geo
        BarcodeType.ISBN -> R.drawable.ic_barcode_type_isbn
        BarcodeType.PHONE -> R.drawable.ic_barcode_type_phone
        BarcodeType.SMS -> R.drawable.ic_barcode_type_sms
        BarcodeType.URL -> R.drawable.ic_barcode_type_url
        BarcodeType.WIFI -> R.drawable.ic_barcode_type_wifi
        else -> R.drawable.ic_barcode_type_other
    }
}