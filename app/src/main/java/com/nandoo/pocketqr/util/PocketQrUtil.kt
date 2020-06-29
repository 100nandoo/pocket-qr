package com.nandoo.pocketqr.util

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.nandoo.pocketqr.R

object PocketQrUtil {

    object Ui {
        fun permissionSnackbar(parent: View, requestMessage: String, action: () -> Unit) {
            Snackbar.make(parent, requestMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.allow) {
                    action()
                }.show()
        }
    }
}
