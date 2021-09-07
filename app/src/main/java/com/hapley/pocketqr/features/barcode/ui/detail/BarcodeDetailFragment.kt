package com.hapley.pocketqr.features.barcode.ui.detail

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.SCREEN_DETAIL
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.databinding.BarcodeDetailDialogLabelBinding
import com.hapley.pocketqr.databinding.BarcodeDetailFragmentBinding
import com.hapley.preview.ui.PreviewFragment
import com.hapley.preview.ui.PreviewItem
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BarcodeDetailFragment : Fragment(R.layout.barcode_detail_fragment) {

    private val binding by viewBinding(BarcodeDetailFragmentBinding::bind)

    private val args by navArgs<BarcodeDetailFragmentArgs>()

    private val viewModel: BarcodeDetailViewModel by viewModels()

    @Inject
    lateinit var tracker: Tracker

    private val screenName: String = SCREEN_DETAIL
    private val className: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
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

        initArgs()
        initUi()
        subscribeUi()
    }

    private fun initArgs() {
        viewModel.id = args.BARCODEID
    }

    private fun initUi() {
        binding.fabEdit.setOnClickListener {
            editLabelDialog()
        }

//        if (viewModel.showTutorial) {
//            initShowcase(fab_edit)
//        }
    }

    private fun subscribeUi() {
        viewModel.barcodeLiveData.observe(
            viewLifecycleOwner,
            {
                binding.tvLabel.text = it.title
                binding.tvLabel.isSelected = true
                binding.tvSubtitle.text = it.subtitle
                binding.tvClickCount.text = it.clickCount.toString()
                binding.tvScannedDate.text = DateUtils.formatDateTime(requireContext(), it.created.time, DateUtils.FORMAT_ABBREV_ALL)
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

                    binding.ivQrcode.load(result.bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    tracker.recordException("Convert rawValue into QR Code Bitmap", e)
                }
            }
        )

        viewModel.previewLiveData.observe(viewLifecycleOwner) { previewItem ->
            binding.ivQrcode.setOnClickListener {
                navigateToPreview(previewItem)
            }
        }
    }

    private fun editLabelDialog() {
        val dialogLabelBinding = BarcodeDetailDialogLabelBinding.inflate(LayoutInflater.from(requireContext()))
        val view = dialogLabelBinding.llParent
        val editText = dialogLabelBinding.editText
        editText.setText(viewModel.barcodeLiveData.value?.title)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.hint_edit_label)
            .setView(view)
            .setPositiveButton(R.string.submit) { _, _ ->
                viewModel.submit(editText.text.toString())
            }
            .show()
    }

    private fun navigateToPreview(previewItem: PreviewItem) {
        findNavController().navigate(R.id.preview_nav_graph, bundleOf(PreviewFragment.PREVIEW_ITEM to previewItem))
    }
}
