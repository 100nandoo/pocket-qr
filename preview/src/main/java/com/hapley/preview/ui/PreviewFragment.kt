package com.hapley.preview.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.hapley.preview.R
import com.hapley.preview.databinding.PreviewFragmentBinding
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

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
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(viewModel.previewItem.rawValue, viewModel.previewItem.zxingFormat, 400, 400)

            binding.imageView.load(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
//            tracker.recordException("Convert rawValue into QR Code Bitmap", e)
        }
    }
}