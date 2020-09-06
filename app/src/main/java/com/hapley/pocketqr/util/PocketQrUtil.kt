package com.hapley.pocketqr.util

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.camera.core.ImageProxy
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.hapley.pocketqr.R
import com.hapley.pocketqr.common.CrashReport
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PocketQrUtil(val context: Context, val clipboardManager: ClipboardManager) {

    private val crashReport by inject(CrashReport::class.java)

    fun permissionSnackbar(parent: View, requestMessage: String, action: () -> Unit) {
        Snackbar.make(parent, requestMessage, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.allow) {
                action()
            }.show()
    }

    fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText(context.getString(R.string.app_name), text)
        clipboardManager.setPrimaryClip(clip)
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
                crashReport.recordException("Check whether Uri can be launch as intent.", e)
                e.localizedMessage
                false
            }
        }
    }

    fun actionShare(context: Context, url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun stringToOptionalUri(string: String?): Optional<Uri>{
        return try {
            Uri.parse(string)
        } catch (e: Exception) {
            crashReport.recordException("Parse Url to Uri", e)
            null
        }.toOptional()
    }

    suspend fun initCustomTabConnection(context: Context): Pair<CustomTabsServiceConnection?, CustomTabsSession> {
        return suspendCancellableCoroutine { cont ->
            val packageName = CustomTabsClient.getPackageName(context, null)

            val connection = object : CustomTabsServiceConnection() {
                override fun onServiceDisconnected(name: ComponentName?) {
                    cont.resumeWithException(Throwable("Service Disconnected!"))
                }

                override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                    try {
                        val session = client.newSession(null)
                        if (session != null) {
                            cont.resume(this to session)
                        } else {
                            cont.resumeWithException(Throwable("Start CustomTabsSession failed!"))
                        }
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                }
            }

            CustomTabsClient.bindCustomTabsService(context, packageName, connection)
        }
    }
}
