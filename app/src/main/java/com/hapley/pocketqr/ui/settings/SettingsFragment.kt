package com.hapley.pocketqr.ui.settings

import android.os.Bundle
import androidx.preference.*
import com.google.android.material.transition.MaterialFadeThrough
import com.hapley.pocketqr.R
import com.hapley.pocketqr.util.BuildUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val CATEGORY_BARCODE = "barcode"
        const val CATEGORY_TUTORIAL = "tutorial"
        const val CATEGORY_ABOUT = "about"

        const val BARCODE_APP_INTRODUCTION = "barcode_app_introduction"

        const val BARCODE_HISTORY_SORT = "barcode_history_sort"
        const val BARCODE_HISTORY_SHOW_TUTORIAL = "barcode_history_show_tutorial"
        const val BARCODE_DETAIL_SHOW_TUTORIAL = "barcode_detail_show_tutorial"

        const val ABOUT_VERSION = "version"
    }

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

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

//                qrCodeCategory.addPreference(
//                    SwitchPreference(context).apply {
//                        key = BARCODE_OPEN_HISTORY_FIRST
//                        title = getString(R.string.open_qr_code_history_first)
//                    }
//                )

                qrCodeCategory.addPreference(
                    ListPreference(context).apply {
                        key = BARCODE_HISTORY_SORT
                        title = getString(R.string.sort_mode)
                        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                        entries = resources.getStringArray(R.array.sort_mode_entries)
                        entryValues = arrayOf(RECENT, MOST_FREQUENT, ALPHABETICAL)
                        setDefaultValue(RECENT)
                    }
                )
            }

            PreferenceCategory(context).let { tutorialCategory ->
                tutorialCategory.key = CATEGORY_TUTORIAL
                tutorialCategory.title = getString(R.string.tutorial)
                screen.addPreference(tutorialCategory)

                tutorialCategory.addPreference(
                    SwitchPreference(context).apply {
                        key = BARCODE_APP_INTRODUCTION
                        title = getString(R.string.show_app_intro)
                    }
                )

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
