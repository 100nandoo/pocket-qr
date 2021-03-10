package com.hapley.pocketqr.features.barcode.ui.history.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialElevationScale
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.SCREEN_BOTTOM_SHEET
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.main.MainViewModel
import com.hapley.pocketqr.util.PocketQrUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.iv_down
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.tv_copy
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.tv_detail
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.tv_favorite
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.tv_open
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.tv_share
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActionBottomSheetDialog : BottomSheetDialogFragment() {

    private val args by navArgs<ActionBottomSheetDialogArgs>()

    @Inject
    lateinit var pocketQrUtil: PocketQrUtil

    private val viewModel: ActionBottomSheetViewModel by viewModels()

    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var tracker: Tracker

    private val screenName: String = SCREEN_BOTTOM_SHEET
    private val className: String = this.javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_history_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initArgs()
        subscribeUi()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1_000L)
            tracker.trackScreen(className, screenName)
        }
    }

    private fun initArgs() {
        viewModel.id = args.BARCODEID
    }

    private fun subscribeUi() {
        viewModel.barcodeLiveData.observe(
            viewLifecycleOwner,
            {
                initUi(it)
            }
        )
    }

    private fun initUi(barcodeItem: BarcodeItem) {
        iv_down.setOnClickListener {
            findNavController().popBackStack()
        }

        tv_detail.transitionName = requireContext().getString(R.string.barcode_history_transition_name, barcodeItem.id)

        tv_detail.setOnClickListener {
            val id = barcodeItem.id.toInt()
            val selectedView = it

            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
            val endTransitionName = getString(R.string.barcode_detail_transition_name)
            val extras = FragmentNavigatorExtras(selectedView to endTransitionName)
            val directions = ActionBottomSheetDialogDirections.actionToBarcodeDetailFragment(id)
            findNavController().navigate(directions, extras)
        }
        tv_share.setOnClickListener {
            actionShare(barcodeItem)
            findNavController().popBackStack()
        }

        tv_open.setOnClickListener {
            mainViewModel.actionOpenUrl(barcodeItem)
            mainViewModel.incrementClickCount(barcodeItem.id.toInt())
        }

        tv_favorite.setOnClickListener {
            actionFavorite()
            findNavController().popBackStack()
        }

        tv_copy.setOnClickListener {
            actionCopyToClipboard(barcodeItem)
            findNavController().popBackStack()
        }
    }

    private fun actionShare(barcodeItem: BarcodeItem) {
        pocketQrUtil.actionShareBarcodeItem(requireContext(), barcodeItem)
    }

    private fun actionFavorite() {
        viewModel.updateFavoriteFlag()
    }

    private fun actionCopyToClipboard(barcodeItem: BarcodeItem) {
        pocketQrUtil.copyToClipboard(barcodeItem)
        pocketQrUtil.shortToast(requireContext(), R.string.copied)
    }
}
