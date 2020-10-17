package com.hapley.preview.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.hapley.preview.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class PreviewFragment : Fragment() {

//    private val args by navArgs<PreviewFragmentArgs>()

    private val viewModel: PreviewViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.preview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initArgs()
    }

    private fun initArgs() {
//        viewModel.id = args.BARCODEID
    }

}