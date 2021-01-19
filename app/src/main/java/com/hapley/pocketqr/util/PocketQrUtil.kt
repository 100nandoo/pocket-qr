package com.hapley.pocketqr.util

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.*
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.Tracker
import com.hapley.pocketqr.features.barcode.ui.BarcodeItem
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PocketQrUtil(private val context: Context, private val clipboardManager: ClipboardManager) {

    private val tracker by inject(Tracker::class.java)

    companion object {
        const val SAFE_ENTRY_REGEX = "-([A-Z]){2}\\w+"
    }

    fun permissionSnackbar(parent: View, requestMessage: String, action: () -> Unit) {
        Snackbar.make(parent, requestMessage, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.allow) {
                action()
            }.show()
    }

    fun copyToClipboard(barcodeItem: BarcodeItem) {
        val clip = ClipData.newPlainText(context.getString(R.string.app_name), barcodeItem.rawValue)
        clipboardManager.setPrimaryClip(clip)
        tracker.copy(barcodeItem)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun toInputImage(imageProxy: ImageProxy?): InputImage? {
        return imageProxy?.image?.let { image ->
            InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        }
    }

    private fun showToast(context: Context, text: CharSequence, duration: Int): Toast {
        return Toast.makeText(context, text, duration)
    }

    fun shortToast(context: Context, text: CharSequence) {
        this.showToast(context, text, Toast.LENGTH_SHORT).show()
    }

    fun shortToast(context: Context, @StringRes resId: Int) {
        this.showToast(context, context.getText(resId), Toast.LENGTH_SHORT).show()
    }

    fun longToast(context: Context, text: CharSequence) {
        this.showToast(context, text, Toast.LENGTH_LONG).show()
    }

    fun longToast(context: Context, @StringRes resId: Int) {
        this.showToast(context, context.getText(resId), Toast.LENGTH_LONG).show()
    }

    fun actionView(context: Context, url: String): Boolean {
        Intent(Intent.ACTION_VIEW).apply {
            return try {
                data = Uri.parse(url)
                if (resolveActivity(context.packageManager) != null) {
                    context.startActivity(this)
                    true
                } else {
                    shortToast(context, "You don't have any app to handle this kind of data!")
                    false
                }
            } catch (e: NullPointerException) {
                tracker.recordException("Check whether Uri can be launch as intent.", e)
                e.localizedMessage
                false
            }
        }
    }

    fun actionShare(context: Context, text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun actionShareBarcodeItem(context: Context, barcodeItem: BarcodeItem) {
        actionShare(context, barcodeItem.rawValue)
        tracker.share(barcodeItem)
    }

    fun launchCustomTab(context: Context, session: CustomTabsSession, uri: Uri) {
        session.mayLaunchUrl(uri, null, null)

        val customTabColorSchemeParams = CustomTabColorSchemeParams
            .Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .build()

        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(customTabColorSchemeParams)
            .setSession(session)
            .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
            .build()
            .launchUrl(context, uri)
    }

    fun stringToOptionalUri(string: String?): Optional<Uri> {
        return try {
            Uri.parse(string)
        } catch (e: Exception) {
            tracker.recordException("Parse Url to Uri", e)
            null
        }.toOptional()
    }

    suspend fun initCustomTabConnection(context: Context): Pair<CustomTabsServiceConnection?, CustomTabsSession> {
        return suspendCancellableCoroutine { cont ->
            val packageName = CustomTabsClient.getPackageName(context, null)

            val connection = object : CustomTabsServiceConnection() {
                override fun onServiceDisconnected(name: ComponentName?) {
                }

                override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                    try {
                        val session = client.newSession(null)
                        if (session != null) {
                            if (cont.isActive.not()) {
                                cont.resume(this to session)
                            }
                        } else {
                            if (cont.isActive.not()) {
                                cont.resumeWithException(Throwable("Start CustomTabsSession failed!"))
                            }
                        }
                    } catch (e: Exception) {
                        if (cont.isActive.not()) {
                            cont.resumeWithException(e)
                        }
                        tracker.recordException("onCustomTabsServiceConnected when try to connect to custom Tabs", e)
                    }
                }
            }

            CustomTabsClient.bindCustomTabsService(context, packageName, connection)
        }
    }

    fun isProbablyAnEmulator() = Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.BOARD == "QC_Reference_Phone"
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HOST.startsWith("Build")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "google_sdk" == Build.PRODUCT

    fun extractSafeEntryLabel(url: String): String {
        return if (url.contains("temperaturepass.ndi-api.gov.sg", true) || url.contains("www.safeentry-qr.gov.sg", true)) {
            val regex = SAFE_ENTRY_REGEX.toRegex()
            val result = regex.find(url)
            result?.value?.replace("-", " ")?.trim() ?: ""
        } else ""
    }
}
