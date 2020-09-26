package com.hapley.pocketqr.features.barcode.ui.history

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.SCREEN_HISTORY
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.ALPHABETICAL
import com.hapley.pocketqr.ui.settings.MOST_FREQUENT
import com.hapley.pocketqr.ui.settings.Mapper
import com.hapley.pocketqr.ui.settings.RECENT
import com.hapley.pocketqr.util.PocketQrUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import kotlinx.android.synthetic.main.barcode_history_fragment.*
import kotlinx.android.synthetic.main.barcode_history_item.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator

class BarcodeHistoryFragment : Fragment(), SimpleSwipeCallback.ItemSwipeCallback {

    private val viewModel: BarcodeHistoryViewModel by viewModel()

    private val pocketQrUtil: PocketQrUtil by inject()

    private val tracker: Tracker by inject()

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(s: String): Boolean {
            itemAdapter.filter(s)
            return true
        }

        override fun onQueryTextChange(s: String): Boolean {
            itemAdapter.filter(s)
            tracker.search(s)
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

    private val itemListImpl: ComparableItemListImpl<BarcodeItem> by lazy {
        ComparableItemListImpl<BarcodeItem>(null)
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
            val isFavorite = viewModel.selectedItemWithPosition.second.isFavorite

            @DrawableRes
            val icon = if (isFavorite) R.drawable.ic_barcode_favorite else R.drawable.ic_barcode_unfavorite

            val iconDrawable = AppCompatResources.getDrawable(requireContext(), icon)
            itemFavorite?.icon = iconDrawable

            return itemFavorite?.icon == iconDrawable
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val selectedItem = viewModel.selectedItemWithPosition
            return when (item.itemId) {
                R.id.item_detail -> {
                    actionNavigateToDetail()
                    mode.finish()
                    true
                }
                R.id.item_share -> {
                    actionShare(selectedItem.second)
                    mode.finish()
                    true
                }
                R.id.item_favorite -> {
                    actionFavorite()
                    mode.finish()
                    true
                }
                R.id.item_copy -> {
                    actionCopyToClipboard(selectedItem.second)
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

    private val screenName: String = SCREEN_HISTORY
    private val className: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_history_fragment, container, false)
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

        initUi()
        subscribeUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
        actionMode = null
        rv_barcode_history.adapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_history, menu)

        val searchView = menu.findItem(R.id.item_search).actionView as SearchView

        searchView.setOnQueryTextListener(queryTextListener)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemId = Mapper.sortModeToMenuItemId(viewModel.sortMode)
        menu.findItem(itemId).isChecked = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_recent -> {
                itemListImpl.withComparator(mergeComparator(scannedDateComparator))
            }
            R.id.item_most_frequent -> {
                itemListImpl.withComparator(mergeComparator(clickCountComparator))
            }
            R.id.item_alphabetical -> {
                itemListImpl.withComparator(mergeComparator(alphabetComparatorAscending))
            }
        }

        val selectedSortMode = Mapper.menuItemIdToSortMode(item.itemId)
        viewModel.updateSortMode(selectedSortMode)

        item.isChecked = !item.isChecked
        return true
    }

    private fun defaultComparator(): Comparator<BarcodeItem> {
        return when (viewModel.sortMode) {
            RECENT -> scannedDateComparator
            MOST_FREQUENT -> clickCountComparator
            ALPHABETICAL -> alphabetComparatorAscending
            else -> scannedDateComparator
        }
    }

    private fun mergeComparator(comparator: Comparator<BarcodeItem>): Comparator<BarcodeItem> {
        return Comparator<BarcodeItem> { lhs, rhs ->
            val favoriteCompare = favoriteComparator.compare(lhs, rhs)
            if (favoriteCompare == 0) {
                comparator.compare(lhs, rhs)
            } else favoriteCompare
        }
    }

    private fun initUi() {
        setHasOptionsMenu(true)

        itemListImpl.withComparator(mergeComparator(defaultComparator()))

        rv_barcode_history.run {
            adapter = fastAdapter
            requireContext().dividerBuilder()
                .size(8, COMPLEX_UNIT_DIP)
                .showFirstDivider()
                .showLastDivider()
                .asSpace().build().addTo(this)
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

        fastAdapter.addEventHook(object : ClickEventHook<BarcodeItem>() {
            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<BarcodeItem>, item: BarcodeItem) {
                viewModel.selectedItemWithPosition = Triple(v, item, position)
                actionNavigateToDetail()
                tracker.selectContent(item)
            }

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return if (viewHolder is BarcodeItem.ViewHolder) {
                    viewHolder.itemView.b_info
                } else null
            }
        })

        fastAdapter.addEventHook(object : ClickEventHook<BarcodeItem>() {
            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<BarcodeItem>, item: BarcodeItem) {
                when {
//                    viewModel.showTutorial -> {
//                        initShowcase(v)
//                    }
                    actionMode == null -> {
                        actionShowBottomSheet(item.id.toInt())
                    }
                    else -> {
                        actionMode?.finish()
                    }
                }

            }

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return if (viewHolder is BarcodeItem.ViewHolder) {
                    viewHolder.itemView.card_history_item
                } else null
            }
        })

        fastAdapter.onPreLongClickListener = { view, _, item, position ->
            view.id
            viewModel.selectedItemWithPosition = Triple(view, item, position)
            actionMode = actionModeHelper.onLongClick(requireActivity() as AppCompatActivity, position)
            actionMode != null
        }

        val touchCallback = SimpleSwipeCallback(this, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_barcode_delete))
            .withSensitivity(6f)
            .withSurfaceThreshold(0.5f)

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_barcode_history)
    }

    private fun subscribeUi() {
        viewModel.barcodesLiveData.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                view_switcher.showNext()
            } else {
                itemListImpl.setNewList(it, true)
            }
        })
    }

    private fun actionCopyToClipboard(barcodeItem: BarcodeItem) {
        pocketQrUtil.copyToClipboard(barcodeItem)
        pocketQrUtil.shortToast(requireContext(), R.string.copied)
    }

    private fun actionShowBottomSheet(id: Int) {
        findNavController().navigate(BarcodeHistoryFragmentDirections.actionShowBottomSheetDialog(id))
    }

    private fun actionNavigateToDetail() {
        val id = viewModel.selectedItemWithPosition.second.id.toInt()
        val selectedView = viewModel.selectedItemWithPosition.first

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        val endTransitionName = getString(R.string.barcode_detail_transition_name)
        val extras = FragmentNavigatorExtras(selectedView to endTransitionName)
        val directions = BarcodeHistoryFragmentDirections.actionToBarcodeDetailFragment(id)
        findNavController().navigate(directions, extras)
    }

    private fun actionShare(barcodeItem: BarcodeItem) {
        pocketQrUtil.actionShare(requireContext(), barcodeItem)
    }

    private fun actionFavorite() {
        viewModel.updateFavoriteFlag()
        fastAdapter.notifyAdapterItemChanged(viewModel.selectedItemWithPosition.third)
        itemListImpl.withComparator(mergeComparator(defaultComparator()))
    }

    private fun actionDelete(position: Int, barcodeItem: BarcodeItem) {
        if (position != RecyclerView.NO_POSITION) {
            viewModel.deleteBarcode(barcodeItem)
        }
    }

    private fun initShowcase(view: View) {
        viewModel.showTutorial = false
    }

    override fun itemSwiped(position: Int, direction: Int) {
        val swipedItem = fastAdapter.getItem(position) ?: return
        if (direction == ItemTouchHelper.LEFT) {
            actionDelete(position, swipedItem)
        }
    }

}
