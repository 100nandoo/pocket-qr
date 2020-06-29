package com.nandoo.pocketqr.common.extension

import android.net.Uri

/**
 * Created by Fernando Fransisco Halim on 2020-01-30.
 */

fun String.rawValueToUri(): Uri? {
    return try {
        Uri.parse(this)
    } catch (e: Exception) {
        null
    }
}
