package com.hapley.pocketqr.common

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class AppPreferences @Inject constructor(val settings: SharedPreferences) {

    companion object {
        object KEY {
            object PdfViewer {
                const val LAST_FILE_PATH = "last_file_path"
                const val LAST_PAGE = ""
            }
        }
    }

    var lastFilePath: String
        get() = settings.getString(KEY.PdfViewer.LAST_FILE_PATH, "") ?: ""
        set(value) = settings.edit {
            putString(KEY.PdfViewer.LAST_FILE_PATH, value)
            putInt(KEY.PdfViewer.LAST_PAGE, 0)
        }

    var lastPage: Int
        get() = settings.getInt(KEY.PdfViewer.LAST_PAGE, 0)
        set(value) = settings.edit { putInt(KEY.PdfViewer.LAST_PAGE, value) }
}
