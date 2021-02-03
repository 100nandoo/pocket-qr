package com.hapley.pocketqr.di

import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.camera.core.Preview
import androidx.preference.PreferenceManager
import androidx.room.Room
import coil.ImageLoader
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.hapley.pocketqr.db.AppDatabase
import com.hapley.pocketqr.db.BarcodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val DATABASE_NAME = "pocket-database"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext appContext: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(appContext)

    @Provides
    @Singleton
    fun provideClipboardManager(@ApplicationContext appContext: Context): ClipboardManager =
        appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext appContext: Context): ImageLoader =
        ImageLoader.Builder(appContext)
            .allowHardware(true)
            .crossfade(true)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
object BarcodeModule {

    @Provides
    @Singleton
    fun provideBarcodeDao(appDatabase: AppDatabase): BarcodeDao = appDatabase.barcodeDao()

    @Provides
    @Singleton
    fun providePreview(): Preview = Preview.Builder().build()

    @Provides
    @Singleton
    fun provideBarcodeScannerOptions(): BarcodeScanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder().build())
}