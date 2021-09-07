package com.hapley.pocketqr.features.barcode.ui.history.bottomsheet

import androidx.annotation.IntDef

const val ACTION_DETAIL = 1
const val ACTION_SHARE = 2
const val ACTION_OPEN = 3
const val ACTION_FAVORITE = 4
const val ACTION_COPY = 5

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [ACTION_DETAIL, ACTION_SHARE, ACTION_OPEN, ACTION_FAVORITE, ACTION_COPY], open = false)
annotation class ActionType
