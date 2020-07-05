package com.nandoo.pocketqr.features.barcode.ui.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.nandoo.pocketqr.R
import kotlinx.android.synthetic.main.barcode_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BarcodeDetailFragment : Fragment() {

    val args by navArgs<BarcodeDetailFragmentArgs>()

    private val viewModel: BarcodeDetailViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initArgs()
        initUi()
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.barcodeLiveData.observe(viewLifecycleOwner, Observer {
            tv_title.text = it.title
            tv_subtitle.text = it.subtitle
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_detail, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_edit_label -> {
                editLabelDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initArgs() {
        viewModel.id = args.BARCODEID
    }

    private fun initUi(){
        setHasOptionsMenu(true)
    }

    private fun editLabelDialog(){
        MaterialDialog(requireContext()).show {
            input(hintRes = R.string.hint_edit_label, prefill = viewModel.barcodeLiveData.value?.title) { dialog, label ->
                viewModel.submit(label.toString())
            }
            positiveButton(R.string.submit)
        }
    }
}