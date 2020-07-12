package com.hapley.pocketqr.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.hapley.pocketqr.R
import com.hapley.pocketqr.util.BuildUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val CATEGORY_BARCODE = "barcode"
        const val CATEGORY_TUTORIAL = "tutorial"
        const val CATEGORY_ABOUT = "about"

        const val BARCODE_SCANNER_SHOW_TUTORIAL = "barcode_scanner_show_tutorial"
        const val BARCODE_HISTORY_SHOW_TUTORIAL = "barcode_history_show_tutorial"
        const val BARCODE_DETAIL_SHOW_TUTORIAL = "barcode_detail_show_tutorial"

        const val ABOUT_VERSION = "version"
    }

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        initUi()
    }

    private fun initUi() {
        val context = preferenceManager.context
        preferenceManager.createPreferenceScreen(context).let { screen ->
//            PreferenceCategory(context).let { qrCodeCategory ->
//                qrCodeCategory.key = CATEGORY_BARCODE
//                qrCodeCategory.title = getString(R.string.qr_code)
//                screen.addPreference(qrCodeCategory)
//
//                qrCodeCategory.addPreference(
//                    SwitchPreference(context).apply {
//                        key = BARCODE_OPEN_HISTORY_FIRST
//                        title = getString(R.string.open_qr_code_history_first)
//                    }
//                )
//            }

            PreferenceCategory(context).let { tutorialCategory ->
                tutorialCategory.key = CATEGORY_TUTORIAL
                tutorialCategory.title = getString(R.string.tutorial)
                screen.addPreference(tutorialCategory)

                tutorialCategory.addPreference(
                    SwitchPreference(context).apply {
                        key = BARCODE_HISTORY_SHOW_TUTORIAL
                        title = getString(R.string.show_tutorial_for, getString(R.string.qr_code), getString(R.string.history).decapitalize())
                    }
                )

                tutorialCategory.addPreference(
                    SwitchPreference(context).apply {
                        key = BARCODE_DETAIL_SHOW_TUTORIAL
                        title = getString(R.string.show_tutorial_for, getString(R.string.qr_code), getString(R.string.detail).decapitalize())
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
