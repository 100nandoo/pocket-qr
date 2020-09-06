package com.hapley.pocketqr.features.barcode.ui.history.bottomsheet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.toOptional
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialElevationScale
import com.hapley.pocketqr.R
import com.hapley.pocketqr.features.barcode.domain.URL
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.barcode_history_bottom_sheet.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActionBottomSheetDialog : BottomSheetDialogFragment() {

    init {
        lifecycleScope.launch {
            whenStarted {
                val result = try {
                   pocketQrUtil.initCustomTabConnection(requireContext())
                } catch (E: Exception) {
                    null
                }.toOptional()

                if(result is Some){
                    connection = result.value.first.toOptional()
                    session = result.value.second.toOptional()
                }
            }
        }
    }

    private val args by navArgs<ActionBottomSheetDialogArgs>()

    private val pocketQrUtil: PocketQrUtil by inject()

    private val viewModel: ActionBottomSheetViewModel by viewModel()

    private var connection: Optional<CustomTabsServiceConnection> = None
    private var session: Optional<CustomTabsSession> = None

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.barcode_history_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initArgs()
        subscribeUi()
    }

    private fun initArgs() {
        viewModel.id = args.BARCODEID
    }

    override fun onDestroy() {
        val connectionTemp = connection
        if(connectionTemp is Some){
            requireActivity().unbindService(connectionTemp.value)
            connection = None
            session = None
        }
        super.onDestroy()
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
            actionShare(barcodeItem.rawValue)
            findNavController().popBackStack()
        }

        tv_open.setOnClickListener {
            actionOpen(barcodeItem)
        }

        tv_favorite.setOnClickListener {
            actionFavorite()
            findNavController().popBackStack()
        }

        tv_copy.setOnClickListener {
            actionCopyToClipboard(barcodeItem.rawValue)
            findNavController().popBackStack()
        }
    }

    private fun actionShare(text: String) {
        pocketQrUtil.actionShare(requireContext(), text)
    }

    private fun actionFavorite() {
        viewModel.updateFavoriteFlag()
    }

    private fun actionCopyToClipboard(text: String) {
        pocketQrUtil.copyToClipboard(text)
        pocketQrUtil.shortToast(requireContext(), R.string.copied)
    }

    private fun actionOpen(barcodeItem: BarcodeItem) {
        when (barcodeItem.barcodeType) {
            URL -> {
                val uri = pocketQrUtil.stringToOptionalUri(barcodeItem.rawValue)

                val tempSession = session
                if (uri is Some && tempSession is Some) {
                    tempSession.value.mayLaunchUrl(uri.value, null, null)

                    CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                        .setSession(tempSession.value)
                        .setStartAnimations(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
                        .setExitAnimations(requireContext(), R.anim.slide_in_left, R.anim.slide_out_right)
                        .build()
                        .launchUrl(requireContext(), Uri.parse(barcodeItem.rawValue))
                    viewModel.incrementClickCount(barcodeItem.id.toInt())

                    findNavController().popBackStack()
                } else {
                    actionIntentViewWrapper(barcodeItem)
                }
            }
            else -> {
                actionIntentViewWrapper(barcodeItem)
            }
        }
    }

    private fun actionIntentViewWrapper(barcodeItem: BarcodeItem) {
        val isSuccess = pocketQrUtil.actionView(requireContext(), barcodeItem.rawValue)
        if (isSuccess) {
            viewModel.incrementClickCount(barcodeItem.id.toInt())
        }
        findNavController().popBackStack()
    }
}