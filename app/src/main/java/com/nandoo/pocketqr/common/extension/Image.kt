package com.nandoo.pocketqr.common.extension

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage

val ImageProxy.toInputImage: InputImage?
    @SuppressLint("UnsafeExperimentalUsageError")
    get() {
        return image?.let { image ->
            InputImage.fromMediaImage(image, this.imageInfo.rotationDegrees)
        }
    }
