package com.hapley.pocketqr.features.barcode.ui.detail

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.hapley.pocketqr.R
import com.hapley.pocketqr.features.barcode.domain.EMAIL
import com.hapley.pocketqr.features.barcode.domain.SMS
import com.hapley.pocketqr.features.barcode.domain.URL
import com.hapley.pocketqr.features.barcode.domain.WIFI
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import kotlinx.android.synthetic.main.barcode_detail_dialog_label.view.*
import kotlinx.android.synthetic.main.barcode_detail_fragment.*
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.scheme.EMail
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url
import net.glxn.qrgen.core.scheme.Wifi
import org.koin.androidx.viewmodel.ext.android.viewModel

class BarcodeDetailFragment : Fragment() {

    private val args by navArgs<BarcodeDetailFragmentArgs>()

    private val viewModel: BarcodeDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(ContextCompat.getColor(requireContext(), R.color.material_color_grey_200))
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initArgs()
        initUi()
        subscribeUi()
    }

    private fun initArgs() {
        viewModel.id = args.BARCODEID
    }

    private fun initUi() {
        fab_edit.setOnClickListener {
            editLabelDialog()
        }

        if (viewModel.showTutorial) {
            initShowcase(fab_edit)
        }
    }

    private fun subscribeUi() {
        viewModel.barcodeLiveData.observe(viewLifecycleOwner, {
            tv_label.text = it.title
            tv_label.isSelected = true
            tv_subtitle.text = it.subtitle
            tv_click_count.text = it.clickCount.toString()
            tv_scanned_date.text = DateUtils.formatDateTime(requireContext(), it.created.time, DateUtils.FORMAT_ABBREV_ALL)
            iv_qrcode.load(generateQrCode(it))
        })
    }

    private fun editLabelDialog() {
        val view = layoutInflater.inflate(R.layout.barcode_detail_dialog_label, null)
        val editText = view.editText
        editText.setText(viewModel.barcodeLiveData.value?.title)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.hint_edit_label)
            .setView(view)
            .setPositiveButton(R.string.submit) { _, _ ->
                viewModel.submit(editText.text.toString())
            }
            .show()

    }

    private fun generateQrCode(barcodeItem: BarcodeItem): Bitmap? {
        // [CONTACT, EMAIL, GEO, ISBN, PHONE, SMS, URL, WIFI, UNKNOWN]
        val schema: Schema? = when (barcodeItem.barcodeType) {
            EMAIL -> EMail.parse(barcodeItem.rawValue)
            URL -> Url.parse(barcodeItem.rawValue)
            SMS -> net.glxn.qrgen.core.scheme.SMS.parse(barcodeItem.rawValue)
            WIFI -> Wifi.parse(barcodeItem.rawValue)
            else -> null
        }

        return schema?.let { QRCode.from(it).bitmap() }
    }

    private fun initShowcase(view: View) {
        FancyShowCaseView.Builder(requireActivity())
            .focusOn(view)
            .title(getString(R.string.tutorial_label))
            .focusShape(FocusShape.CIRCLE)
            .enableAutoTextPosition()
            .build()
            .show()

        viewModel.showTutorial = false
    }

}