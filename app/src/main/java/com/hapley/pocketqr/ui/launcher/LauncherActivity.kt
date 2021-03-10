package com.hapley.pocketqr.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hapley.pocketqr.main.MainActivity
import com.hapley.pocketqr.ui.appintro.AppIntroActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.showTutorial.not()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AppIntroActivity::class.java))
            viewModel.showTutorial = false
        }
        finishAffinity()
    }
}
