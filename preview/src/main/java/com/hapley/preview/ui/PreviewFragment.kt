package com.hapley.preview.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.hapley.preview.R
import com.hapley.preview.databinding.PreviewFragmentBinding
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class PreviewFragment : Fragment() {

    companion object {
        const val PREVIEW_ITEM = "previewItem"
    }

    private val binding by viewBinding(PreviewFragmentBinding::bind)

    private val viewModel: PreviewViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.preview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initUi()
    }

    private fun initArgs() {
        val previewItem = arguments?.getParcelable<PreviewItem>(PREVIEW_ITEM)
        if (previewItem != null) {
            viewModel.previewItem = previewItem
        } else {
            findNavController().popBackStack()
        }
    }

    private fun initUi() {
        try {
            val previewItem = viewModel.previewItem
            (binding.imageView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "${previewItem.ratio.first}:${previewItem.ratio.second}"
            val height = 400
            val width = (height * previewItem.ratio.first).roundToInt()

            val bitmap: Bitmap = BarcodeEncoder().encodeBitmap(previewItem.rawValue, previewItem.zxingFormat, width, height)

            binding.imageView.load(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
//            tracker.recordException("Convert rawValue into QR Code Bitmap", e)
        }
    }
}