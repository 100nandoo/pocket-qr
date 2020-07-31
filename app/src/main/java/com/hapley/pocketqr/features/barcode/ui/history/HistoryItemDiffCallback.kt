package com.hapley.pocketqr.features.barcode.ui.history

import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.mikepenz.fastadapter.diff.DiffCallback

class HistoryItemDiffCallback: DiffCallback<BarcodeItem> {
    override fun areContentsTheSame(oldItem: BarcodeItem, newItem: BarcodeItem): Boolean = oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: BarcodeItem, newItem: BarcodeItem): Boolean = oldItem == newItem

    override fun getChangePayload(oldItem: BarcodeItem, oldItemPosition: Int, newItem: BarcodeItem, newItemPosition: Int): Any? = null
}