package com.hapley.pocketqr.features.barcode.ui.scanner

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.SCREEN_SCANNER
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.main.MainViewModel
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.barcode_scanner_fragment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class BarcodeScannerFragment : Fragment() {

    private val viewModel: BarcodeScannerViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private val tracker: Tracker by inject()
    private val pocketQrUtil: PocketQrUtil by inject()
    private val preview: Preview by inject()
    private val scanner: BarcodeScanner by inject()

    private var cameraControl: CameraControl? = null

    private val screenName: String = SCREEN_SCANNER
    private val className: String = this.javaClass.simpleName

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

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(2_000L)
            tracker.trackScreen(className, screenName)
        }
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
                    val camera = if (pocketQrUtil.isProbablyAnEmulator()) {
                        this.bindToLifecycle(this@BarcodeScannerFragment, cameraSelector, preview)
                    } else {
                        this.bindToLifecycle(this@BarcodeScannerFragment, cameraSelector, preview, imageAnalysis)
                    }
                    cameraControl = camera.cameraControl
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    setUpPinchToZoom(camera)

                } catch (e: Exception) {
                    tracker.recordException("1. Bind camera\n2. Setup camera control\n3. Set surface provider.", e)
                    Timber.e(e)
                }
            }
        }

        slider.addOnChangeListener { _, value, _ ->
            cameraControl?.setLinearZoom(value)
        }

        slider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) = Unit

            override fun onStopTrackingTouch(slider: Slider) {
                tracker.zoom(slider.value)
            }
        })

    }

    private fun setUpPinchToZoom(camera: Camera) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val zoomState = camera.cameraInfo.zoomState.value

                val currentZoomRatio: Float = zoomState?.zoomRatio ?: 0F
                val result = currentZoomRatio * detector.scaleFactor
                cameraControl?.setZoomRatio(result)

                val linearZoom: Float = zoomState?.linearZoom ?: 0F
                slider.value = linearZoom
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                val zoomState = camera.cameraInfo.zoomState.value
                val linearZoom: Float = zoomState?.linearZoom ?: 0F
                tracker.zoom(linearZoom)
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(context, listener)

        previewView.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            v.performClick()
            return@setOnTouchListener true
        }
    }

    private fun handleBarcode(barcode: Barcode) {
        val rawValue = barcode.rawValue.toString()
        Timber.d("Barcode raw value : $rawValue")

        if (viewModel.tempRawValue != rawValue) {
            viewModel.insertBarcode(barcode)

            val barcodeItem = viewModel.convertToDomain(barcode, 0)?.let { BarcodeItem(it) }

            barcodeItem?.let {
                tracker.scan(barcodeItem)
            }

            Snackbar.make(qr_code_parent, rawValue, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open)) { mainViewModel.actionOpenUrl(barcodeItem) }
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
