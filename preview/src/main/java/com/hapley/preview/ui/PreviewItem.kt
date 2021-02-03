package com.hapley.preview.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PreviewItem (val label: String, val rawValue: String) : Parcelable