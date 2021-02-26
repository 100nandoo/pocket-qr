package com.hapley.pocketqr.features.barcode.ui.history

import android.view.View
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyTouchHelper
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem

interface IBarcodeHistoryAdapterHelper {
    fun actionDelete(position: Int, barcodeItem: BarcodeItem?)
}

class BarcodeHistoryAdapterHelper(
    private val recyclerView: EpoxyRecyclerView,
    private val iBarcodeHistoryAdapterHelper: IBarcodeHistoryAdapterHelper
) {

    init {
        setupSwipe()
    }

    private fun setupSwipe() {
        EpoxyTouchHelper.initSwiping(recyclerView)
            .right()
            .withTarget(BarcodeItem::class.java)
            .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<BarcodeItem>() {
                override fun onSwipeCompleted(model: BarcodeItem?, itemView: View?, position: Int, direction: Int) {
                    iBarcodeHistoryAdapterHelper.actionDelete(position, model)
                }
            })
    }
}