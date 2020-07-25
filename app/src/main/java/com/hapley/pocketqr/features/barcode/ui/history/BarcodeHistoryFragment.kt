package com.hapley.pocketqr.features.barcode.ui.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hapley.pocketqr.R
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.util.PocketQrUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
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

    private lateinit var selectExtension: SelectExtension<BarcodeItem>
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val selectedItem = viewModel.selectedItem
            if (selectedItem != null) {
                return when (item.itemId) {
                    R.id.item_detail -> {
                        actionNavigateToDetail(selectedItem.id.toInt())
                        mode.finish()
                        true
                    }
                    R.id.item_share -> {
                        true
                    }
                    R.id.item_favorite -> {
                        true
                    }
                    R.id.item_copy -> {
                        copyToClipboard(selectedItem.rawValue)
                        mode.finish()
                        true
                    }
                    else -> false
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
        }
    }

    private val actionModeHelper by lazy {
        ActionModeHelper(fastAdapter, R.menu.menu_barcode_history_action_mode, actionModeCallback)
            .withTitleProvider(object : ActionModeHelper.ActionModeTitleProvider {
                override fun getTitle(selected: Int): String {
                    return selectExtension.selectedItems.firstOrNull()?.title ?: ""
                }
            })
    }

    private var actionMode: ActionMode? = null


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

        val searchView = menu.findItem(R.id.item_search).actionView as SearchView

        searchView.setOnQueryTextListener(queryTextListener)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initUi() {
        setHasOptionsMenu(true)

        rv_barcode_history.run {
            adapter = fastAdapter
        }
        selectExtension = fastAdapter.getSelectExtension()
        selectExtension.apply {
            isSelectable = true
            multiSelect = false
            selectOnLongClick = true
        }

        fastAdapter.onPreClickListener = { _, _, item, _ ->
            val res = actionModeHelper.onClick(item)
            res ?: false
        }

        fastAdapter.onClickListener = { view, _, item, _ ->
            if (view != null) {
                if (viewModel.showTutorial) {
                    initShowcase(view)
                } else {
                    pocketQrUtil.actionView(requireContext(), item.rawValue)
                    actionMode?.finish()
                }
            }
            false
        }

        fastAdapter.onPreLongClickListener = { _, _, item, position ->
            viewModel.selectedItem = item
            actionMode = actionModeHelper.onLongClick(requireActivity() as AppCompatActivity, position)
            actionMode != null
        }
    }

    private fun subscribeUi() {
        viewModel.barcodesLiveData.observe(viewLifecycleOwner, Observer {
            itemAdapter.set(it)
        })
    }

    private fun copyToClipboard(text: String) {
        pocketQrUtil.copyToClipboard(text)
        pocketQrUtil.shortToast(requireContext(), R.string.copied)
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
