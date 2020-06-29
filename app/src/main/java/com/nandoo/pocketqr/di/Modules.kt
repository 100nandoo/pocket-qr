package com.nandoo.pocketqr.di

import android.content.ClipboardManager
import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.nandoo.pocketqr.common.AppPreferences
import com.nandoo.pocketqr.features.barcode.data.BarcodeRepository
import com.nandoo.pocketqr.db.AppDatabase
import com.nandoo.pocketqr.features.barcode.domain.BarcodeUseCase
import com.nandoo.pocketqr.features.barcode.ui.history.BarcodeHistoryViewModel
import com.nandoo.pocketqr.features.barcode.ui.scanner.BarcodeScannerViewModel
import com.nandoo.pocketqr.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

val appModule: Module = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, Modules.DATABASE_NAME).build() }
    single { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
    single { AppPreferences(get()) }
    single { androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    viewModel { SettingsViewModel() }
}

val barcodeModule: Module = module {
    single { get<AppDatabase>().barcodeDao() }
    single { BarcodeRepository(get()) }
    single { BarcodeUseCase(barcodeRepository = get()) }
    viewModel { BarcodeScannerViewModel(barcodeUseCase = get()) }
    viewModel { BarcodeHistoryViewModel(barcodeUseCase = get()) }
}

object Modules {
    const val DATABASE_NAME = "pocket-database"
}
