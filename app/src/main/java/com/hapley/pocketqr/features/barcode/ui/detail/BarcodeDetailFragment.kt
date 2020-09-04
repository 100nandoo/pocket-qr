package com.hapley.pocketqr.features.barcode.ui.detail

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.CrashReport
import kotlinx.android.synthetic.main.barcode_detail_dialog_label.view.*
import kotlinx.android.synthetic.main.barcode_detail_fragment.*
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BarcodeDetailFragment : Fragment() {

    private val args by navArgs<BarcodeDetailFragmentArgs>()

    private val viewModel: BarcodeDetailViewModel by viewModel()

    private val crashReport: CrashReport by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
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
            try {
                val renderOption = RenderOption().apply {
                    content = it.rawValue
                    borderWidth = 16
                    patternScale = 1f
                    color = Color(
                        auto = false,
                        background = ContextCompat.getColor(requireContext(), R.color.material_color_white),
                        light = ContextCompat.getColor(requireContext(), R.color.material_color_white),
                        dark = ContextCompat.getColor(requireContext(), R.color.black_900)
                    )
                }
                val result = AwesomeQrRenderer.render(renderOption)

                iv_qrcode.load(result.bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
                crashReport.recordException("Convert rawValue into QR Code Bitmap", e)
            }
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