package com.hapley.pocketqr.common

import android.content.SharedPreferences
import androidx.core.content.edit
import khronos.months
import javax.inject.Inject

class AppPreferences @Inject constructor(val settings: SharedPreferences) {

    companion object {
        object KEY {
            object Review {
                const val LAST_ASK_FOR_REVIEW = "last_ask_for_review"
            }
        }
    }

    var lastAskForReview: Long
        get() = settings.getLong(KEY.Review.LAST_ASK_FOR_REVIEW, 4.months.ago.time)
        set(value) = settings.edit {
            putLong(KEY.Review.LAST_ASK_FOR_REVIEW, value)
        }

}