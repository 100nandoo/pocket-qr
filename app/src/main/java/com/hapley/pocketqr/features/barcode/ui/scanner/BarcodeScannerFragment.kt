package com.hapley.pocketqr.features.barcode.ui.scanner

import android.os.Bundle
import android.view.*
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.showSystemAppDetailsPage
import com.google.android.gms.ads.AdRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.common.extension.actionView
import com.hapley.pocketqr.ui.settings.SettingsFragment
import com.hapley.pocketqr.util.BuildUtil
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.barcode_scanner_fragment.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import org.koin.androidx.scope.lifecycleScope as koinScope

class BarcodeScannerFragment : Fragment() {

    private val viewModel: BarcodeScannerViewModel by viewModel()

    private val appPreferences: AppPreferences by inject()

    private val pocketQrUtil: PocketQrUtil by inject()

    private val preview: Preview by koinScope.inject()

    private val scanner: BarcodeScanner by koinScope.inject()

    private var cameraControl: CameraControl? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_scanner_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkPreferences()
        initUi()
        requestPermission()
        initAds()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_scanner, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_history -> {
                findNavController().navigate(BarcodeScannerFragmentDirections.actionToBarcodeHistoryFragment())
                true
            }
            R.id.item_settings -> {
                findNavController().navigate(BarcodeScannerFragmentDirections.actionToSettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkPreferences() {
        viewModel.openBarcodeHistoryFirst = appPreferences.settings
            .getBoolean(SettingsFragment.BARCODE_OPEN_HISTORY_FIRST, false)
    }

    private fun initUi() {
        if (viewModel.openBarcodeHistoryFirst.not()) {
            setHasOptionsMenu(true)
        }

    }

    private fun requestPermission() {
        askForPermissions(Permission.CAMERA) { result ->
            when {
                result[Permission.CAMERA] == GrantResult.GRANTED -> {
                    setupCameraAndQrCodeDetector()
                }
                result[Permission.CAMERA] == GrantResult.PERMANENTLY_DENIED -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.permission_required)
                        .setMessage(getString(R.string.please_allow_access, getString(R.string.camera)))
                        .setPositiveButton(R.string.yes) { _, _ ->
                            findNavController().navigateUp()
                            showSystemAppDetailsPage()
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            findNavController().navigateUp()
                        }.show()
                }
                else -> {
                    pocketQrUtil.permissionSnackbar(
                        qr_code_parent,
                        getString(R.string.please_allow_access, getString(R.string.camera))
                    ) { requestPermission() }
                }
            }
        }
    }

    private fun setupCameraAndQrCodeDetector() {
        lifecycleScope.launch {
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            ProcessCameraProvider.getInstance(requireContext()).await().apply {
                this.unbindAll()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), BarcodeAnalyzer(scanner) { handleBarcode(it) }) }

                try {
                    val camera = this.bindToLifecycle(this@BarcodeScannerFragment, cameraSelector, preview, imageAnalysis)
                    cameraControl = camera.cameraControl
                    preview.setSurfaceProvider(previewView.createSurfaceProvider())
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }

        slider.addOnChangeListener { slider, value, fromUser ->
            cameraControl?.setLinearZoom(value)
        }
    }

    private fun handleBarcode(barcode: Barcode) {
        val rawValue = barcode.rawValue.toString()
        Timber.d("Barcode raw value : $rawValue")

        if (viewModel.tempRawValue != rawValue) {
            viewModel.setBarcode(barcode)

            Snackbar.make(qr_code_parent, rawValue, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open)) { this@BarcodeScannerFragment.requireContext().actionView(rawValue) }
                .show()
        }
    }

    private fun initAds() {
        adView.isVisible = BuildUtil.isPro.not()
        if (BuildUtil.isPro.not()) {
            val request = AdRequest.Builder().build()
            adView.loadAd(request)
        }
    }
}
