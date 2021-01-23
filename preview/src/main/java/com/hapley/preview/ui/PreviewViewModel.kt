package com.hapley.preview.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor() : ViewModel() {

    lateinit var previewItem: PreviewItem
}