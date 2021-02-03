package com.hapley.pocketqr.features.barcode.ui.detail

import androidx.core.content.edit
import androidx.lifecycle.*
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.features.barcode.domain.BarcodeUseCase
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import com.hapley.pocketqr.ui.settings.SettingsFragment
import com.hapley.preview.ui.PreviewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BarcodeDetailViewModel @Inject constructor(val barcodeUseCase: BarcodeUseCase, private val appPreferences: AppPreferences) : ViewModel() {

    var id = -1

    var showTutorial: Boolean
        get() = appPreferences.settings.getBoolean(SettingsFragment.BARCODE_DETAIL_SHOW_TUTORIAL, true)
        set(value) {
            appPreferences.settings.edit { putBoolean(SettingsFragment.BARCODE_DETAIL_SHOW_TUTORIAL, value) }
        }

    val barcodeLiveData: LiveData<BarcodeItem> by lazy {
        Transformations.map(barcodeUseCase.getByIdLiveData(id)) { barcode -> BarcodeItem(barcode) }
    }

    val previewLiveData: LiveData<PreviewItem> by lazy {
        barcodeUseCase.getPreviewLiveDataById(id)
    }

    fun submit(label: String) {
        if (id > -1) {
            viewModelScope.launch(Dispatchers.IO) {
                barcodeLiveData.value?.title = label
                barcodeUseCase.updateLabel(label, id)
                Timber.v("Finish!")
            }
        }
    }
}