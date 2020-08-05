package com.hapley.pocketqr.ui.launcher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hapley.pocketqr.MainActivity
import com.hapley.pocketqr.ui.appintro.AppIntroActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherActivity : AppCompatActivity() {

    private val viewModel: LauncherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(viewModel.showTutorial.not()){
            finishAffinity()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AppIntroActivity::class.java))
            viewModel.showTutorial = false
        }
    }
}