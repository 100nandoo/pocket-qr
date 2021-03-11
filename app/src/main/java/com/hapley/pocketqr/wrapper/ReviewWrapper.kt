package com.hapley.pocketqr.wrapper

import android.app.Activity
import android.content.Context
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.hapley.pocketqr.common.AppPreferences
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.util.PocketQrUtil
import dagger.hilt.android.qualifiers.ActivityContext
import khronos.month
import khronos.week
import java.util.Date
import javax.inject.Inject

class ReviewWrapper @Inject constructor(
    val appPreferences: AppPreferences,
    @ActivityContext context: Context,
    val pocketQrUtil: PocketQrUtil,
    private val tracker: Tracker
) {

    private val manager = ReviewManagerFactory.create(context)

    /**
     * High Level Function
     * Trigger In-App Review
     */
    suspend fun askForReview(activity: Activity) {
        if (allowToAskForReview()) {
            launchReview(activity)
        }
    }

    /**
     * Low Level Function
     * Wrapper function for In-App Review
     * @see com.google.android.play.core.review.ReviewInfo
     * @see com.google.android.play.core.review.ReviewManagerFactory
     */
    private suspend fun requestReviewInfo(): ReviewInfo? {
        return try {
            val request = manager.requestReview()
            request
        } catch (e: Exception) {
            tracker.recordException("Request Review", e)
            null
        }
    }

    private suspend fun launchReview(activity: Activity) {
        val reviewInfo = requestReviewInfo()
        reviewInfo?.let {
            val date = Date()
            manager.launchReview(activity, reviewInfo)
            tracker.askForReview(date)
            appPreferences.lastAskForReview = date.time
        }
    }

    /**
     * Low Level Function
     * Trigger In-App Review Logic
     */
    private fun aWeekUser(): Boolean {
        return 1.week.ago > pocketQrUtil.firstInstallTime() ?: Date()
    }

    private fun askForReviewRecently(): Boolean {
        val millis = appPreferences.lastAskForReview
        val date = Date(millis)
        return 3.month.ago > date
    }

    private fun allowToAskForReview(): Boolean = aWeekUser() && askForReviewRecently()
}