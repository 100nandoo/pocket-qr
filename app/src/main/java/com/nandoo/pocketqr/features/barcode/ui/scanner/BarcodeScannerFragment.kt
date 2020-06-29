package com.nandoo.pocketqr.features.barcode.ui.scanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.showSystemAppDetailsPage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.common.AppPreferences
import com.nandoo.pocketqr.common.extension.actionView
import com.nandoo.pocketqr.ui.settings.SettingsFragment
import com.nandoo.pocketqr.util.MLKitVision
import com.nandoo.pocketqr.util.PocketQrUtil
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.android.synthetic.main.barcode_scanner_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class BarcodeScannerFragment : Fragment() {

    private val viewModel: BarcodeScannerViewModel by viewModel()

    private val appPreferences: AppPreferences by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_scanner_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkPreferences()
        initUi()
        requestPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_scanner, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_barcode_history -> {
                findNavController().navigate(BarcodeScannerFragmentDirections.actionQrcodeScannerFragmentToBarcodeHistoryFragment())
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
                    PocketQrUtil.Ui.permissionSnackbar(
                        qr_code_parent,
                        getString(R.string.please_allow_access, getString(R.string.camera))
                    ) { requestPermission() }
                }
            }
        }
    }

    private fun setupCameraAndQrCodeDetector() {
        camera_view_kit.apply {
            audio = Audio.OFF
            setLifecycleOwner(viewLifecycleOwner)
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
            addFrameProcessor { frame ->
                frameProcessor(frame)
            }
        }
    }

    private fun frameProcessor(frame: Frame) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            ).build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(MLKitVision.toInputImage(frame))
            .addOnSuccessListener { barcodes ->

                if (barcodes.isNotEmpty()) {

                    val barcode = barcodes.first()
                    val rawValue = barcode.rawValue.toString()

                    if (viewModel.tempRawValue != rawValue) {
                        viewModel.setBarcode(barcode)

                        Snackbar.make(qr_code_parent, rawValue, Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.open)) {
                                try {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(rawValue)))
                                } catch (e: Exception) {
                                    Timber.w(e)
                                }
                            }.show()
                    }
                }
            }
    }
}
