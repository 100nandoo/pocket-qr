package com.hapley.pocketqr.features.barcode.ui.scanner

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.CrashReport
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.barcode_scanner_fragment.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class BarcodeScannerFragment : Fragment() {

    private val viewModel: BarcodeScannerViewModel by viewModel()
    private val crashReport: CrashReport by inject()
    private val pocketQrUtil: PocketQrUtil by inject()
    private val preview: Preview by inject()
    private val scanner: BarcodeScanner by inject()

    private var cameraControl: CameraControl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_scanner_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupCameraAndQrCodeDetector()
        initAds()
    }

    private fun setupCameraAndQrCodeDetector() {
        lifecycleScope.launch {
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            ProcessCameraProvider.getInstance(requireContext()).await().apply {
                this.unbindAll()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(requireContext()),
                            BarcodeAnalyzer(scanner, pocketQrUtil) { handleBarcode(it) })
                    }

                try {
                    val camera =  if(BuildUtil.isDebug){
                        this.bindToLifecycle(this@BarcodeScannerFragment, cameraSelector, preview)
                    } else {
                        this.bindToLifecycle(this@BarcodeScannerFragment, cameraSelector, preview, imageAnalysis)
                    }
                    cameraControl = camera.cameraControl
                    preview.setSurfaceProvider(previewView.createSurfaceProvider())
                } catch (e: Exception) {
                    crashReport.recordException("1. Bind camera\n2. Setup camera control\n3. Set surface provider.", e)
                    Timber.e(e)
                }
            }
        }

        slider.addOnChangeListener { _, value, _ ->
            cameraControl?.setLinearZoom(value)
        }
    }

    private fun handleBarcode(barcode: Barcode) {
        val rawValue = barcode.rawValue.toString()
        Timber.d("Barcode raw value : $rawValue")

        if (viewModel.tempRawValue != rawValue) {
            viewModel.setBarcode(barcode)

            Snackbar.make(qr_code_parent, rawValue, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open)) { pocketQrUtil.actionView(requireContext(), rawValue) }
                .show()
        }
    }

    private val adSize: AdSize
        get() {
            val display = requireActivity().windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = adCointainerView.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth)
        }

    private fun initAds() {
        val shouldShowAds = BuildUtil.isPro.not()
        adCointainerView.isVisible = shouldShowAds
        if (shouldShowAds) {
            val adView = AdView(requireContext()).apply {
                adUnitId = getString(R.string.ads_ids)
                adSize = this@BarcodeScannerFragment.adSize
            }
            adCointainerView.addView(adView)

            val request = AdRequest.Builder().build()
            adView.loadAd(request)
        }
    }
}
