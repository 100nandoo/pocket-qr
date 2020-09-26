package com.hapley.pocketqr.di

import android.content.ClipboardManager
import android.content.Context
import androidx.camera.core.Preview
import androidx.preference.PreferenceManager
import androidx.room.Room
import coil.ImageLoader
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.db.AppDatabase
import com.hapley.pocketqr.features.barcode.data.BarcodeRepository
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.detail.BarcodeDetailViewModel
import com.hapley.pocketqr.features.barcode.ui.history.BarcodeHistoryViewModel
import com.hapley.pocketqr.features.barcode.ui.history.bottomsheet.ActionBottomSheetViewModel
import com.hapley.pocketqr.features.barcode.ui.scanner.BarcodeScannerViewModel
import com.hapley.pocketqr.main.MainViewModel
import com.hapley.pocketqr.ui.launcher.LauncherViewModel
import com.hapley.pocketqr.ui.settings.SettingsViewModel
import com.hapley.pocketqr.util.PocketQrUtil
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

val appModule: Module = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, Modules.DATABASE_NAME).fallbackToDestructiveMigration().build() }
    single { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
    single { AppPreferences(settings = get()) }
    single { androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    single { PocketQrUtil(context = androidContext(), clipboardManager = get()) }
    single { Tracker() }
    single { ImageLoader.Builder(androidApplication()).allowHardware(true).crossfade(true).build() }
    viewModel { MainViewModel(barcodeUseCase = get()) }
    viewModel { SettingsViewModel() }
    viewModel { LauncherViewModel(appPreferences = get()) }
}

val barcodeModule: Module = module {
    single { get<AppDatabase>().barcodeDao() }
    single { BarcodeRepository(barcodeDao = get()) }
    single { BarcodeUseCase(barcodeRepository = get(), tracker = get()) }
    viewModel { BarcodeScannerViewModel(barcodeUseCase = get(), pocketQrUtil = get()) }
    viewModel { BarcodeHistoryViewModel(barcodeUseCase = get(), appPreferences = get(), tracker = get()) }
    viewModel { ActionBottomSheetViewModel(barcodeUseCase = get()) }
    viewModel { BarcodeDetailViewModel(barcodeUseCase = get(), appPreferences = get()) }

    single { Preview.Builder().build() }
    single { BarcodeScanning.getClient(BarcodeScannerOptions.Builder().build()) }
}

val fakeModule: Module = module {
    single { MockDataGenerator(barcodeRepository = get()) }
}

object Modules {
    const val DATABASE_NAME = "pocket-database"
}
