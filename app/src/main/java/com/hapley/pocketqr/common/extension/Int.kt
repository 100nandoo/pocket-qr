package com.hapley.pocketqr.common.extension

import android.content.res.Resources

/**
 * Created by Fernando Fransisco Halim on 2020-01-28.
 */

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()