package com.hapley.preview.di

import com.hapley.preview.ui.PreviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val previewModule: Module = module {
    viewModel { PreviewViewModel() }
}
