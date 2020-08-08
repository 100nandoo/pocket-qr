package com.hapley.pocketqr.features.barcode.ui.history

import android.os.Bundle
import android.view.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import kotlinx.android.synthetic.main.barcode_history_fragment.*
import kotlinx.coroutines.flow.merge
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

    private val alphabetComparatorAscending: Comparator<BarcodeItem> by lazy {
        Comparator<BarcodeItem> { lhs, rhs -> lhs.title.compareTo(rhs.title) }
    }

    private val favoriteComparator: Comparator<BarcodeItem> by lazy {
        Comparator<BarcodeItem> { lhs, rhs -> rhs.isFavorite.compareTo(lhs.isFavorite) }
    }

    private val clickCountComparator: Comparator<BarcodeItem> by lazy {
        Comparator<BarcodeItem> { lhs, rhs -> rhs.clickCount.compareTo(lhs.clickCount) }
    }

    private val scannedDateComparator: Comparator<BarcodeItem> by lazy {
        Comparator<BarcodeItem> { lhs, rhs -> rhs.created.compareTo(lhs.created) }
    }

    private val mergeComparator by lazy {
        Comparator<BarcodeItem> { lhs, rhs ->
            val favoriteCompare = favoriteComparator.compare(lhs, rhs)
            if (favoriteCompare == 0) {
                alphabetComparatorAscending.compare(lhs, rhs)
            } else favoriteCompare
        }
    }

    private val itemListImpl: ComparableItemListImpl<BarcodeItem> by lazy {
        ComparableItemListImpl(mergeComparator)
    }

    private val itemAdapter: ItemAdapter<BarcodeItem> by lazy {
        ItemAdapter(itemListImpl)
            .also { it.itemFilter.filterPredicate = filterPredicate }
    }

    private val fastAdapter = FastAdapter.with(itemAdapter)

    private lateinit var selectExtension: SelectExtension<BarcodeItem>

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            val itemFavorite = menu.findItem(R.id.item_favorite)
            val isFavorite = viewModel.selectedItemWithPosition.first.isFavorite

            @DrawableRes
            val icon = if (isFavorite) R.drawable.ic_barcode_favorite else R.drawable.ic_barcode_unfavorite

            val iconDrawable = ContextCompat.getDrawable(requireContext(), icon)
            itemFavorite?.icon = iconDrawable

            return itemFavorite?.icon == iconDrawable
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val selectedItem = viewModel.selectedItemWithPosition
            return when (item.itemId) {
                R.id.item_detail -> {
                    actionNavigateToDetail(selectedItem.first.id.toInt())
                    mode.finish()
                    true
                }
                R.id.item_share -> {
                    actionShare(selectedItem.first.rawValue)
                    mode.finish()
                    true
                }
                R.id.item_favorite -> {
                    actionFavorite()
                    mode.finish()
                    true
                }
                R.id.item_copy -> {
                    actionCopyToClipboard(selectedItem.first.rawValue)
                    mode.finish()
                    true
                }
                else -> false
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_default -> {
                itemListImpl.withComparator(mergeComparator)
            }
            R.id.item_click_count -> {
                itemListImpl.withComparator(clickCountComparator)
            }
            R.id.item_scanned_date -> {
                itemListImpl.withComparator(scannedDateComparator)
            }
            R.id.item_alphabetical -> {
                itemListImpl.withComparator(alphabetComparatorAscending)
            }
        }
        item.isChecked = !item.isChecked
        return true
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
                    val isSuccess = pocketQrUtil.actionView(requireContext(), item.rawValue)
                    if (isSuccess) {
                        viewModel.incrementClickCount(item.id.toInt())
                    }
                    actionMode?.finish()
                }
            }
            false
        }

        fastAdapter.onPreLongClickListener = { _, _, item, position ->
            viewModel.selectedItemWithPosition = Pair(item, position)
            actionMode = actionModeHelper.onLongClick(requireActivity() as AppCompatActivity, position)
            actionMode != null
        }
    }

    private fun subscribeUi() {
        viewModel.barcodesLiveData.observe(viewLifecycleOwner, Observer {
            itemListImpl.setNewList(it, true)
        })
    }

    private fun actionCopyToClipboard(text: String) {
        pocketQrUtil.copyToClipboard(text)
        pocketQrUtil.shortToast(requireContext(), R.string.copied)
    }

    private fun actionNavigateToDetail(id: Int) {
        findNavController().navigate(BarcodeHistoryFragmentDirections.actionToBarcodeDetailFragment(id))
    }

    private fun actionShare(text: String) {
        pocketQrUtil.actionShare(requireContext(), text)
    }

    private fun actionFavorite() {
        viewModel.updateFavoriteFlag()
        fastAdapter.notifyAdapterItemChanged(viewModel.selectedItemWithPosition.second)
        itemListImpl.withComparator(mergeComparator)
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
