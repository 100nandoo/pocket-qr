package com.hapley.preview.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.zxing.BarcodeFormat as ZxingFormat

@Parcelize
data class PreviewItem(val label: String, val rawValue: String, val zxingFormat: ZxingFormat, val ratio: Pair<Float, Float>) : Parcelable