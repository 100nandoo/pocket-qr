package com.nandoo.pocketqr.common.extension

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes

private fun Context.showToast(text: CharSequence, duration: Int): Toast {
    return Toast.makeText(this, text, duration)
}

fun Context.shortToast(text: CharSequence) {
    this.showToast(text, Toast.LENGTH_SHORT).show()
}

fun Context.shortToast(@StringRes resId: Int) {
    this.showToast(this.getText(resId), Toast.LENGTH_SHORT).show()
}

fun Context.longToast(text: CharSequence) {
    this.showToast(text, Toast.LENGTH_LONG).show()
}

fun Context.longToast(@StringRes resId: Int) {
    this.showToast(this.getText(resId), Toast.LENGTH_LONG).show()
}

fun Context.actionView(url: String) {
    Intent(ACTION_VIEW).apply {
        try {
            data = Uri.parse(url)
            if (resolveActivity(packageManager) != null) {
                startActivity(this)
            }
        } catch (e: NullPointerException){
            e.localizedMessage
        }
    }
}