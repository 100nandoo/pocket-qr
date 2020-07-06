package com.nandoo.pocketqr.features.barcode.ui.history

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.common.extension.actionView
import com.nandoo.pocketqr.common.extension.shortToast
import com.nandoo.pocketqr.features.barcode.ui.BarcodeItem
import com.nandoo.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.barcode_history_fragment.*
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class BarcodeHistoryFragment : Fragment() {

    private val viewModel: BarcodeHistoryViewModel by viewModel()

    private val pocketQrUtil: PocketQrUtil by inject()

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(s: String): Boolean {
            itemAdapter.filter(s)
            return true
        }

        override fun onQueryTextChange(s: String): Boolean {
            itemAdapter.filter(s)
            return true
        }
    }

    private val filterPredicate = { item: BarcodeItem, c: CharSequence? ->
        val title = item.subtitle.toLowerCase(Locale.getDefault())
        val subtitle = item.title.toLowerCase(Locale.getDefault())
        val constraint = c.toString().toLowerCase(Locale.getDefault())
        title.contains(constraint) || subtitle.contains(constraint)
    }

    private val itemAdapter: ItemAdapter<BarcodeItem> by lazy {
        ItemAdapter<BarcodeItem>()
            .also { it.itemFilter.filterPredicate = filterPredicate }
    }

    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_history_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initUi()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_history, menu)

        menu.findItem(R.id.item_barcode_scan).isVisible = viewModel.openBarcodeHistoryFirst

        val searchView = menu.findItem(R.id.item_search).actionView as SearchView

        searchView.setOnQueryTextListener(queryTextListener)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_barcode_scan -> {
                findNavController().navigate(BarcodeHistoryFragmentDirections.actionToBarcodeScannerFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUi() {
        setHasOptionsMenu(true)

        rv_barcode_history.run {
            adapter = fastAdapter
        }

        fastAdapter.onClickListener = { view, _, item, _ ->
            if (viewModel.showTutorial && view != null) {
                initShowcase(view)
            } else {
                this.requireContext().actionView(item.rawValue)
            }
            false
        }

        fastAdapter.onLongClickListener = { _, _, item, _ ->
            actionNavigateToDetail(item.id.toInt())
            false
        }
    }

    private fun subscribeUi() {
        viewModel.barcodesLiveData.observe(viewLifecycleOwner, Observer {
            itemAdapter.set(it)
        })
    }

    private fun copyToClipboard(text: String) {
        pocketQrUtil.copyToClipboard(text)
        this.requireContext().shortToast(R.string.copied)
    }

    private fun actionNavigateToDetail(id: Int) {
        findNavController().navigate(BarcodeHistoryFragmentDirections.actionToBarcodeDetailFragment(id))
    }

    private fun initShowcase(view: View) {
        FancyShowCaseView.Builder(requireActivity())
            .focusOn(view)
            .title("Tap on the item to open the QR code link. \n\nTap on hold to go into the detail.")
            .focusShape(FocusShape.CIRCLE)
            .focusCircleRadiusFactor(0.5)
            .enableAutoTextPosition()
            .build()
            .show()

        viewModel.showTutorial = false
    }
}
