package com.nandoo.pocketqr.features.barcode.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import com.fondesa.recyclerviewdivider.addDivider
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.common.AppPreferences
import com.nandoo.pocketqr.common.extension.actionView
import com.nandoo.pocketqr.common.extension.dpToPx
import com.nandoo.pocketqr.common.extension.shortToast
import com.nandoo.pocketqr.features.barcode.ui.BarcodeItem
import com.nandoo.pocketqr.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.barcode_history_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BarcodeHistoryFragment : Fragment() {

    private val viewModel: BarcodeHistoryViewModel by viewModel()

    private val appPreferences: AppPreferences by inject()

    private val clipboardManager: ClipboardManager by inject()

    private val itemAdapter: ItemAdapter<BarcodeItem> by lazy {
        ItemAdapter<BarcodeItem>()
    }

    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_history_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkPreferences()
        initUi()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_history, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_barcode_scan -> {
                findNavController().navigate(BarcodeHistoryFragmentDirections.actionBarcodeHistoryFragmentToQrcodeScannerFragment())
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
        if (viewModel.openBarcodeHistoryFirst) {
            setHasOptionsMenu(true)
        }

        rv_barcode_history.run {
            adapter = fastAdapter
            addDivider()

            context.dividerBuilder()
                .color(ContextCompat.getColor(requireContext(), R.color.material_color_grey_100))
                .size(8.dpToPx)
                .showFirstDivider()
                .build()
                .addTo(this)

        }

        fastAdapter.onClickListener = { _, _, item, _ ->
            this.requireContext().actionView(item.barcode.rawValue)
            false
        }

        fastAdapter.onLongClickListener = { _, _, item, _ ->
            val clip = ClipData.newPlainText(getString(R.string.app_name), item.barcode.title)
            clipboardManager.setPrimaryClip(clip)
            this.requireContext().shortToast(R.string.copied)
            false
        }
    }

    private fun subscribeUi() {
        viewModel.barcodesLiveData.observe(viewLifecycleOwner, Observer {
            FastAdapterDiffUtil[itemAdapter] = it
        })
    }
}
