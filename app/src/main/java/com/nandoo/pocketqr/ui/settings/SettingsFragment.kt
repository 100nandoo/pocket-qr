package com.nandoo.pocketqr.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.nandoo.pocketqr.R
import com.nandoo.pocketqr.util.BuildUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val CATEGORY_BARCODE = "barcode"
        const val CATEGORY_ABOUT = "about"

        const val BARCODE_OPEN_HISTORY_FIRST = "barcode_open_history_first"


        const val ABOUT_VERSION = "version"
    }

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        initUi()
    }

    private fun initUi() {
        val context = preferenceManager.context
        preferenceManager.createPreferenceScreen(context).let { screen ->
            PreferenceCategory(context).let { qrCodeCategory ->
                qrCodeCategory.key = CATEGORY_BARCODE
                qrCodeCategory.title = getString(R.string.qr_code)
                screen.addPreference(qrCodeCategory)

                qrCodeCategory.addPreference(
                    SwitchPreference(context).apply {
                        key = BARCODE_OPEN_HISTORY_FIRST
                        title = getString(R.string.open_qr_code_history_first)
                    }
                )
            }

            PreferenceCategory(context).let { aboutCategory ->
                aboutCategory.key = CATEGORY_ABOUT
                aboutCategory.title = getString(R.string.about)
                screen.addPreference(aboutCategory)

                aboutCategory.addPreference(
                    Preference(context).apply {
                        key = ABOUT_VERSION
                        title = getString(R.string.build_version)
                        summary = BuildUtil.versionName
                    }
                )
            }

            preferenceScreen = screen
        }
    }
}
