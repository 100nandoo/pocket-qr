package com.hapley.pocketqr.features.barcode.ui.history.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActionBottomSheetDialog : BottomSheetDialogFragment() {

    private val args by navArgs<ActionBottomSheetDialogArgs>()

    private val pocketQrUtil: PocketQrUtil by inject()

    private val viewModel: ActionBottomSheetViewModel by viewModel()

    private val mainViewModel: MainViewModel by sharedViewModel()

    private val tracker: Tracker by inject()

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
        viewModel.barcodeLiveData.observe(viewLifecycleOwner, {
            initUi(it)
        })
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
        pocketQrUtil.actionShare(requireContext(), barcodeItem)
    }

    private fun actionFavorite() {
        viewModel.updateFavoriteFlag()
    }

    private fun actionCopyToClipboard(barcodeItem: BarcodeItem) {
        pocketQrUtil.copyToClipboard(barcodeItem)
        pocketQrUtil.shortToast(requireContext(), R.string.copied)
    }
}