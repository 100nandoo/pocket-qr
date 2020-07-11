package com.hapley.pocketqr.util

import android.graphics.ImageFormat
import com.google.mlkit.vision.common.InputImage
import com.otaliastudios.cameraview.frame.Frame

object MLKitVision {
    fun toInputImage(frame: Frame): InputImage {
        return InputImage.fromByteArray(frame.getData(), frame.size.width, frame.size.width, frame.rotationToUser, ImageFormat.NV21)
    }
}
