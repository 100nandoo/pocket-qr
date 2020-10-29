package com.hapley.pocketqr.main

import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.toOptional
import com.hapley.pocketqr.R
import com.hapley.pocketqr.features.barcode.domain.URL
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.features.barcode.ui.toShortcutInfo
import com.hapley.pocketqr.util.PocketQrUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    init {
        lifecycleScope.launch {
            whenStarted {
                val result = try {
                    pocketQrUtil.initCustomTabConnection(this@MainActivity)
                } catch (E: Exception) {
                    null
                }.toOptional()

                if (result is Some) {
                    connection = result.value.first.toOptional()
                    session = result.value.second.toOptional()
                }
            }
        }
    }

    private val viewModel: MainViewModel by viewModel()

    private val pocketQrUtil: PocketQrUtil by inject()

    private var connection: Optional<CustomTabsServiceConnection> = None
    var session: Optional<CustomTabsSession> = None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()

        subscribeUi()
    }

    private fun initToolbar() {
        setSupportActionBar(main_toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.barcode_scanner_fragment,
                R.id.barcode_history_fragment,
                R.id.settings_fragment
            )
        )
        main_toolbar.setupWithNavController(navController, appBarConfiguration)
        bottom_nav.setupWithNavController(navController)
    }

    private fun subscribeUi() {
        viewModel.starredBarcodesLiveData.observe(this) {
            updateDynamicShortcut(it.mapNotNull { barcodeItem -> barcodeItem.toShortcutInfo(this@MainActivity) })
        }

        viewModel.barcodeItemLiveData().observe(this) {
            actionOpen(it)
        }
    }


    override fun onStop() {
        super.onStop()
//        val connectionTemp = connection
//        if (connectionTemp is Some) {
//            unbindService(connectionTemp.value)
//            connection = None
//            session = None
//        }
    }

    private fun updateDynamicShortcut(list: List<ShortcutInfo>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = getSystemService<ShortcutManager>()
            shortcutManager?.dynamicShortcuts = list
        }

    }

    private fun actionOpen(barcodeItem: BarcodeItem) {
        when (barcodeItem.barcodeType) {
            URL -> {
                val uri = pocketQrUtil.stringToOptionalUri(barcodeItem.rawValue)
                val session = session

                if (uri is Some && session is Some) {
                    pocketQrUtil.launchCustomTab(this, session.value, uri.value)
                    viewModel.incrementClickCount(barcodeItem.id.toInt())
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
        val isSuccess = pocketQrUtil.actionView(this, barcodeItem.rawValue)
        if (isSuccess) {
            viewModel.incrementClickCount(barcodeItem.id.toInt())
        }
    }
}