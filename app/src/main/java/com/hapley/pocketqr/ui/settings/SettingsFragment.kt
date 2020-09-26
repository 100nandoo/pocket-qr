package com.hapley.pocketqr.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.google.android.material.transition.MaterialFadeThrough
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.SCREEN_SETTINGS
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.util.BuildUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val CATEGORY_BARCODE = "barcode"
        const val CATEGORY_DISPLAY = "display"
        const val CATEGORY_TUTORIAL = "tutorial"
        const val CATEGORY_ABOUT = "about"

        const val BARCODE_APP_INTRODUCTION = "barcode_app_introduction"

        const val BARCODE_HISTORY_SORT = "barcode_history_sort"
        const val NIGHT_MODE = "night_mode"
        const val BARCODE_HISTORY_SHOW_TUTORIAL = "barcode_history_show_tutorial"
        const val BARCODE_DETAIL_SHOW_TUTORIAL = "barcode_detail_show_tutorial"

        const val ABOUT_VERSION = "version"
    }

    private val viewModel: SettingsViewModel by viewModel()

    private val tracker: Tracker by inject()

    private val screenName: String = SCREEN_SETTINGS
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        initUi()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(2_000L)
            tracker.trackScreen(className, screenName)
        }
    }

    private fun initUi() {
        val context = preferenceManager.context
        preferenceManager.createPreferenceScreen(context).let { screen ->
            PreferenceCategory(context).let { qrCodeCategory ->
                qrCodeCategory.key = CATEGORY_BARCODE
                qrCodeCategory.title = getString(R.string.qr_code)
                qrCodeCategory.isIconSpaceReserved = false
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
                        isIconSpaceReserved = false
                        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                        entries = resources.getStringArray(R.array.sort_mode_entries)
                        entryValues = arrayOf(RECENT, MOST_FREQUENT, ALPHABETICAL)
                        setDefaultValue(RECENT)
                        setOnPreferenceChangeListener { _, newValue ->
                            Timber.d("setOnPreferenceChangeListener")
                            if (value != newValue && newValue is String) {
                                tracker.sort(newValue)
                            }
                            true
                        }
                    }
                )

            }

            PreferenceCategory(context).let { displayCategory ->
                displayCategory.key = CATEGORY_DISPLAY
                displayCategory.title = getString(R.string.display)
                displayCategory.isIconSpaceReserved = false
                screen.addPreference(displayCategory)

                displayCategory.addPreference(
                    ListPreference(context).apply {
                        key = NIGHT_MODE
                        title = getString(R.string.night_mode)
                        isIconSpaceReserved = false
                        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                        entries = resources.getStringArray(R.array.night_mode_entries)
                        entryValues = arrayOf(FOLLOW_SYSTEM, DARK, LIGHT)
                        setDefaultValue(FOLLOW_SYSTEM)

                        onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                            val nightMode = newValue as String
                            val nightModeStatic = Mapper.nightModetoNightModeStatic(nightMode)
                            AppCompatDelegate.setDefaultNightMode(nightModeStatic)
                            true
                        }
                    }
                )

            }


            PreferenceCategory(context).let { tutorialCategory ->
                tutorialCategory.key = CATEGORY_TUTORIAL
                tutorialCategory.title = getString(R.string.tutorial)
                tutorialCategory.isIconSpaceReserved = false
                screen.addPreference(tutorialCategory)

                tutorialCategory.addPreference(
                    SwitchPreference(context).apply {
                        key = BARCODE_APP_INTRODUCTION
                        title = getString(R.string.show_app_intro)
                        isIconSpaceReserved = false
                    }
                )

//                tutorialCategory.addPreference(
//                    SwitchPreference(context).apply {
//                        key = BARCODE_HISTORY_SHOW_TUTORIAL
//                        title = getString(R.string.show_tutorial_for, getString(R.string.qr_code), getString(R.string.history).decapitalize())
//                        isIconSpaceReserved = false
//                    }
//                )
//
//                tutorialCategory.addPreference(
//                    SwitchPreference(context).apply {
//                        key = BARCODE_DETAIL_SHOW_TUTORIAL
//                        title = getString(R.string.show_tutorial_for, getString(R.string.qr_code), getString(R.string.detail).decapitalize())
//                        isIconSpaceReserved = false
//                    }
//                )
            }


            PreferenceCategory(context).let { aboutCategory ->
                aboutCategory.key = CATEGORY_ABOUT
                aboutCategory.title = getString(R.string.about)
                aboutCategory.isIconSpaceReserved = false
                screen.addPreference(aboutCategory)

                aboutCategory.addPreference(
                    Preference(context).apply {
                        key = ABOUT_VERSION
                        title = getString(R.string.build_version)
                        summary = BuildUtil.versionName
                        isIconSpaceReserved = false
                    }
                )
            }

            preferenceScreen = screen
        }
    }
}
