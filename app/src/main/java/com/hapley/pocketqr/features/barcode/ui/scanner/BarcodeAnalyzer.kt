package com.hapley.pocketqr.features.barcode.ui.scanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BarcodeAnalyzer(
    private val scanner: BarcodeScanner,
    private val pocketQrUtil: PocketQrUtil,
    private val listener: (barcode: Barcode) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        pocketQrUtil.toInputImage(image)?.let {
            GlobalScope.launch {
                val barcode = scanner.process(it).await().firstOrNull()
                if (barcode != null) {
                    listener(barcode)
                }
                image.close()
            }
        }
    }
}