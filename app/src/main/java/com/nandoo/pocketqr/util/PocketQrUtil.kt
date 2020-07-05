package com.nandoo.pocketqr.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.common.extension.shortToast

class PocketQrUtil(val context: Context, val clipboardManager: ClipboardManager) {

    fun permissionSnackbar(parent: View, requestMessage: String, action: () -> Unit) {
        Snackbar.make(parent, requestMessage, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.allow) {
                action()
            }.show()
    }

    fun copyToClipboard(text: String){
        val clip = ClipData.newPlainText(context.getString(R.string.app_name), text)
        clipboardManager.setPrimaryClip(clip)
    }
}
