package com.nandoo.pocketqr.util

import android.graphics.ImageFormat
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.otaliastudios.cameraview.frame.Frame

object FirebaseVisionUtil {
    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    fun toFirebaseVisionImage(frame: Frame): FirebaseVisionImage {
        val imageRotation = degreesToFirebaseRotation(frame.rotation)

        val firebaseVisionMetadata = FirebaseVisionImageMetadata
            .Builder()
            .setFormat(ImageFormat.NV21)
            .setHeight(frame.size.height)
            .setRotation(imageRotation)
            .setWidth(frame.size.width)
            .build()

        return FirebaseVisionImage.fromByteArray(frame.getData(), firebaseVisionMetadata)
    }
}
