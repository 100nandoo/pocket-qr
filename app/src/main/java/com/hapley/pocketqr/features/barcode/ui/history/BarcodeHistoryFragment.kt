package com.hapley.pocketqr.features.barcode.ui.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.SCREEN_HISTORY
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.databinding.BarcodeHistoryFragmentBinding
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.features.barcode.ui.BarcodeItemListener
import com.hapley.pocketqr.ui.settings.Mapper
import com.hapley.pocketqr.util.PocketQrUtil
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BarcodeHistoryFragment : Fragment(R.layout.barcode_history_fragment) {

    private val binding by viewBinding(BarcodeHistoryFragmentBinding::bind)

    private val viewModel: BarcodeHistoryViewModel by viewModels()

    @Inject
    lateinit var pocketQrUtil: PocketQrUtil

    @Inject
    lateinit var tracker: Tracker

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(s: String): Boolean {
            viewModel.calculate(s)
            tracker.search(s)
            return true
        }

        override fun onQueryTextChange(s: String): Boolean {viewModel.calculate(s)
            tracker.search(s)
            return true
        }
    }

    private val queryCloseListener = SearchView.OnCloseListener {
        viewModel.calculate()
        false
    }

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

//    private val actionModeHelper by lazy {
//        ActionModeHelper(fastAdapter, R.menu.menu_barcode_history_action_mode, actionModeCallback)
//            .withTitleProvider(object : ActionModeHelper.ActionModeTitleProvider {
//                override fun getTitle(selected: Int): String {
//                    return selectExtension.selectedItems.firstOrNull()?.title ?: ""
//                }
//            })
//    }

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

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(2_000L)
            tracker.trackScreen(className, screenName)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        subscribeUi()
        initSwipe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
        actionMode = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_barcode_history, menu)

        val searchView = menu.findItem(R.id.item_search).actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(queryTextListener)
            setOnCloseListener {
                viewModel.calculate()
                false
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemId = Mapper.sortModeToMenuItemId(viewModel.sortMode)
        menu.findItem(itemId).isChecked = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Mapper.menuItemIdToSortMode(item.itemId)?.let {
            viewModel.updateSortMode(it)
        }

        item.isChecked = !item.isChecked
        return true
    }

    private fun initUi() {
        setHasOptionsMenu(true)
        binding.epoxyRvBarcodeHistory.setItemSpacingDp(8)
        viewModel.calculate()
    }

    private val barcodeItemListener = object : BarcodeItemListener {
        override fun clickListener(id: Int) {
            actionShowBottomSheet(id)
        }
    }

    private fun subscribeUi() {
        viewModel.barcodeListLiveData.observe(viewLifecycleOwner, { barcodeItemList ->
            if (barcodeItemList.isEmpty()) {
                binding.viewSwitcher.showNext()
            } else {
                binding.epoxyRvBarcodeHistory.withModels {
                    barcodeItemList.forEach { barcodeItemViewBinding ->
                        barcodeItemViewBinding.listener = barcodeItemListener

                        barcodeItemViewBinding
                            .id(barcodeItemViewBinding.id)
                            .addTo(this)
                    }
                }
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
        pocketQrUtil.actionShareBarcodeItem(requireContext(), barcodeItem)
    }

    private fun actionFavorite() {
        viewModel.updateFavoriteFlag()
    }

    private val actionDelete: IBarcodeHistoryAdapterHelper = object : IBarcodeHistoryAdapterHelper {
        override fun actionDelete(position: Int, barcodeItem: BarcodeItem?) {
            if (position != RecyclerView.NO_POSITION && barcodeItem != null) {
                viewModel.deleteBarcode(barcodeItem)
            }
        }
    }

    private fun initSwipe() {
        val barcodeHistoryAdapterHelper = BarcodeHistoryAdapterHelper(binding.epoxyRvBarcodeHistory, actionDelete)
    }

}
