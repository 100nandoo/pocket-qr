package com.hapley.pocketqr.util

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.camera.core.ImageProxy
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.hapley.pocketqr.R

class PocketQrUtil(val context: Context, val clipboardManager: ClipboardManager) {

    fun permissionSnackbar(parent: View, requestMessage: String, action: () -> Unit) {
        Snackbar.make(parent, requestMessage, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.allow) {
                action()
            }.show()
    }

    fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText(context.getString(R.string.app_name), text)
        clipboardManager.setPrimaryClip(clip)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun toInputImage(imageProxy: ImageProxy?): InputImage? {
        return imageProxy?.image?.let { image ->
            InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        }
    }
}
