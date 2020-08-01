package com.hapley.pocketqr.ui.appintro

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hapley.pocketqr.MainActivity
import com.hapley.pocketqr.R

class AppIntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setImmersiveMode()
        setTransformer(AppIntroPageTransformerType.Fade)
        isWizardMode = true

        isIndicatorEnabled = true

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.welcome),
                description = getString(R.string.keep_history),
                backgroundDrawable = R.drawable.background_gradient_purple,
                titleTypefaceFontRes = R.font.work_sans_bold,
                descriptionTypefaceFontRes = R.font.work_sans_medium
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.label_with_emoji),
                description = getString(R.string.label_description),
                backgroundDrawable = R.drawable.background_gradient_blue,
                titleTypefaceFontRes = R.font.work_sans_bold,
                descriptionTypefaceFontRes = R.font.work_sans_medium
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.auto_focus),
                description = getString(R.string.auto_focus_description),
                backgroundDrawable = R.drawable.background_gradient_green,
                titleTypefaceFontRes = R.font.work_sans_bold,
                descriptionTypefaceFontRes = R.font.work_sans_medium
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.camera_permission),
                description = getString(R.string.camera_permission_desc),
                backgroundDrawable = R.drawable.background_gradient_orange,
                titleTypefaceFontRes = R.font.work_sans_bold,
                descriptionTypefaceFontRes = R.font.work_sans_medium
            )
        )


        askForPermissions(
            permissions = arrayOf(Manifest.permission.CAMERA),
            slideNumber = 4,
            required = true
        )
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        navigateToMainActivity()
    }

    override fun onUserDisabledPermission(permissionName: String) {
        super.onUserDisabledPermission(permissionName)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_required)
            .setMessage(getString(R.string.please_allow_access, getString(R.string.camera)))
            .setPositiveButton(R.string.yes) { _, _ -> showSystemAppDetailsPage() }
            .setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }

    private fun navigateToMainActivity() {
        finishAffinity()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showSystemAppDetailsPage() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        })
    }
}