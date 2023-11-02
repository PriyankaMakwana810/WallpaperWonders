package com.techiq.wallpaperwonders.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.intuit.sdp.BuildConfig
import com.techiq.wallpaperwonders.R

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@SuppressLint("SdCardPath", "StaticFieldLeak")
object Constant {

    const val SHARED_COMMON = "SharedCommon"
    const val BASE_URL = "BaseUrl"
    const val API_CLIENT = "ApiClient"
    const val OK_HTTPS = "okHttps"
    const val GSON = "gson"
    const val SERVICE_FOR_LOGIN_GSON = "ServiceForLoginGson"
    const val SERVICE_WITH_GSON = "ServiceWithGson"
    const val SERVICE_WITH_GSON_SIGNIN = "ServiceWithGsonSignIN"
    const val IS_LOGIN = "isLogin"
    const val LOGOUT_SCREEN = "logoutScreen"
    const val LOGOUT = "logout"
    const val TABLE_LOGIN = "UserData"
    const val INTENT_LOGIN_USER_NAME = "INTENT_LOGIN_USER_NAME"
    const val INTENT_LOGIN_PASSWORD = "INTENT_LOGIN_PASSWORD"


    fun getDateFormat(date: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date)
        return SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH).format(format ?: Date())
    }

    private lateinit var dialog: Dialog
    private lateinit var wvProgressText: WebView
    private lateinit var btCancelDownload: Button
    private lateinit var clProgressDialog: ConstraintLayout
    fun showProgress(context: Context) {
        try {
            dialog = FullWidthDialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) dialog.setContentView(R.layout.progress_dialog_from_33)
            else dialog.setContentView(R.layout.progress_dialog)
            wvProgressText = dialog.findViewById(R.id.wvProgressText)
            btCancelDownload = dialog.findViewById(R.id.btCancelDownload)
            clProgressDialog = dialog.findViewById(R.id.clProgressDialog)
            wvProgressText.visibility = View.INVISIBLE
            btCancelDownload.visibility = View.INVISIBLE
            wvProgressText.settings.loadWithOverviewMode = true
            wvProgressText.settings.useWideViewPort = true
            wvProgressText.settings.javaScriptEnabled = true
            dialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.localizedMessage?.let { logE("catch Error", it) }
        }
    }


    fun dismissProgress(context: Context) {

        try {
            if (::dialog.isInitialized && dialog.isShowing) {
                dialog.dismiss()
                if (::wvProgressText.isInitialized) wvProgressText.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { logE("catch Error", it) }
        }
    }


    private fun getKeyNameFromJson(productsJson: JSONObject): Pair<ArrayList<String>, ArrayList<String>> {
        val keyNameArray = arrayListOf<String>()
        val valueArray = arrayListOf<String>()
        val iter: Iterator<String> = productsJson.keys()
        while (iter.hasNext()) {
            val key = iter.next()
            if (key != "id" && key != "strSalesName") {
                val newValue = key.replace("T", " T", false).replace("S", " S").replace("P", " P")
                    .replace("febrauary", "February", true).replace("Febraury", "February", true)
                keyNameArray.add(newValue.trim())
                valueArray.add(productsJson.optString(key))
            }
        }
        return Pair(keyNameArray, valueArray)
    }


    fun getSubString(mainString: String, lastString: String, startString: String): String {
        var endString = mainString
        if (mainString.length > 2 && (mainString.contains("(") && mainString.contains(")")) || (mainString.contains(
                "<"
            ) && mainString.contains(">"))
        ) {
            val endIndex = endString.indexOf(lastString)
            val startIndex = endString.indexOf(startString) + 1
            logI("message", "" + endString.substring(startIndex, endIndex))
            endString = endString.substring(startIndex, endIndex)
        }
        return endString.trim()
    }


    fun convertToDoubleString(double: String): String {
        val newDouble = try {
            double.toDouble()
        } catch (e: Exception) {
            e.localizedMessage?.let { logE("catch Error", it) }
            0.0
        }
        return String.format(Locale.ENGLISH, "%.2f", newDouble)
    }

    fun String?.toDoubleCustom(): Double {
        val newDouble = try {
            this?.toDouble()
        } catch (e: Exception) {
            e.localizedMessage?.let { logE("catch Error", it) }
            0.0
        }
        val data = String.format(Locale.ENGLISH, "%.2f", newDouble)
        return data.toDouble()
    }

    fun String?.toDoubleCustom3Decimal(): Double {
        val newDouble = try {
            this?.toDouble()
        } catch (e: Exception) {
            e.localizedMessage?.let { logE("catch Error", it) }
            0.0
        }
        val data = String.format(Locale.ENGLISH, "%.3f", newDouble)
        return data.toDouble()
    }

    fun Double?.toStringCustom(): String {
        return String.format(Locale.ENGLISH, "%.2f", this)
    }

    fun convertToDoubleString(double: Double): String {
        return String.format(Locale.ENGLISH, "%.2f", double)
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return df.format(c)
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        return df.format(c)
    }

    fun convertDateFormat(date: String): String {
        return try {
            var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            if (newDate != null) {
                val convertedDate = spf.format(newDate)
                convertedDate
            } else "-"
        } catch (e: Exception) {
            e.localizedMessage?.let { logE("catch Error", it) }
            "-"
        }
    }

    fun convertTimeFormat(date: String): String {
        return try {
            var spf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
            if (newDate != null) {
                val convertedDate = spf.format(newDate)
                convertedDate
            } else "-"
        } catch (e: Exception) {
            e.localizedMessage?.let { logE("catch Error", it) }
            "-"
        }
    }

    fun convertStringDateToLong(dateString: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = sdf.parse(dateString)
            date?.time ?: 0L
        } catch (e: ParseException) {
            e.localizedMessage?.let { logE("catch Error", it) }
            0L
        }
    }

    @SuppressLint("HardwareIds")
    fun getSubscriptionId(context: Context): String {
        val deviceId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val tsLong = System.currentTimeMillis() / 1000
        return "$tsLong-$deviceId"
    }

    fun convertStringToRequestBody(data: String): RequestBody {
        return data.toRequestBody("text/xml".toMediaTypeOrNull())
    }

    fun isNetWork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun smallToastWithContext(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun longToastWithContext(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun Context.smallToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.longToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun logI(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.i("TAG", "$tag: $msg")
    }

    fun logE(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.e("TAG", "$tag: $msg")
    }

    fun logE(tag: String, msg: String, throwable: Throwable) {
        if (BuildConfig.DEBUG) Log.e("TAG", "$tag: $msg", throwable)
    }

    fun logD(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.d("TAG", "$tag: $msg")
    }

    fun logW(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.w("TAG", "$tag: $msg")
    }

    private fun escapeUnicode(input: String): String {
        val b = StringBuilder(input.length)
        val f = Formatter(b)
        for (c in input.toCharArray()) {
            if (c.code < 128) {
                b.append(c)
            } else {
                f.format("\\u%04x", c.code)
            }
        }
        return b.toString()
    }

    fun dipToPixels(context: Context, dipValue: Int): Float {
        val scale: Float = context.resources.displayMetrics.scaledDensity
        return context.resources.getDimensionPixelSize(dipValue) / scale
    }

    fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target ?: "") && Patterns.EMAIL_ADDRESS.matcher(target ?: "")
            .matches()
    }

    fun bodyToString(context: Context, request: Request?): String {
        return if (request != null) try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            if (copy.body != null) {
                copy.body!!.writeTo(buffer)
            }
            buffer.readUtf8()
        } catch (e: Exception) {
            e.printStackTrace()
            "did not work"
        } catch (e: SocketException) {
            e.printStackTrace()
            "did not work"
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            "did not work"
        } else "did not work"
    }


    fun String.decodeString(): String {

        return try {
            val decodedString =
                java.net.URLDecoder.decode(this, java.nio.charset.StandardCharsets.UTF_8.toString())
            decodedString
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            this
        } catch (e: Exception) {
            e.printStackTrace()
            this
        }
    }

    fun String?.toFormattedDateString(): String {
        return if (this?.isDigitsOnly() == true) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.ENGLISH)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = this@toFormattedDateString.toLong()
            }
            dateFormat.format(calendar.time)
        } else {
            ""
        }
    }

    private fun String.isDigitsOnly(): Boolean {
        return this.all { it.isDigit() }
    }

    fun TextView.copyTextToClipboard() {
        val textToCopy = this.text.toString()

        val clipboardManager =
            this.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this.context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun makeItBold(boldText: String, otherText: String): SpannableText {
        val apiRequestFull = "$boldText ${otherText.decodeString()}"
        return SpannableText.Builder(apiRequestFull).bold(0, boldText.length).build()
    }

    fun Activity.hideSoftKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    // Restart the app
    fun Context.restartApp() {
        logI("App Funstion", "restart App")
        val packageManager = applicationContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        val componentName = intent?.component

        val mainIntent = Intent.makeRestartActivityTask(componentName)
        applicationContext.startActivity(mainIntent)

        // Finish the current activity
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }


    private fun deleteRecursive(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            val children = fileOrDirectory.listFiles()
            if (children != null) {
                for (child in children) {
                    val success = deleteRecursive(child)
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return fileOrDirectory.delete()
    }


    private fun copyFile(src: File, dest: File) {
        FileInputStream(src).use { fis ->
            FileOutputStream(dest).use { os ->
                val buffer = ByteArray(1024)
                var len: Int
                while (fis.read(buffer).also { len = it } != -1) {
                    os.write(buffer, 0, len)
                }
            }
        }
    }

    // Show the countdown dialog
    @OptIn(DelicateCoroutinesApi::class)
    fun Context.showCountdownDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            val dialog = CountdownDialog(this)
            dialog.show()

            GlobalScope.launch {
                for (i in 5 downTo 0) {
                    delay(1000) // Wait for 1 second

                    withContext(Dispatchers.Main) {
                        dialog.updateCountdown(i)
                    }
                }
                restartApp()
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                }
            }
        }, 1000)
    }

    fun getCurrentDateString(): String? {
        val date: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return df.format(date)
    }

    fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getBitmapFromByteArray(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    fun TextView.timerConversion(millisUntilFinished: Long) {
        val seconds = (millisUntilFinished / 1000) % 60
        val minutes = (millisUntilFinished / (1000 * 60)) % 60
        val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
        val days = (millisUntilFinished / (1000 * 60 * 60 * 24))

        val newMin = if (minutes < 10) "0$minutes" else minutes
        val newHours = if (hours < 10) "0$hours" else hours
        val newSec = if (seconds < 10) "0$seconds" else seconds
        val newDay = if (days < 10) "0$days" else days

        this.text = "Remaining time: " + if (days > 0) "${days}d : $newHours : $newMin : $newSec"
        else if (hours > 0) "$newHours : $newMin : $newSec"
        else if (minutes > 0) "$newMin : $newSec"
        else if (seconds > 0) "$newSec"
        else "${newDay}d : $newHours : $newMin : $newSec"
    }


    fun Double?.changeDoubleToStringWith2Decimal(): String {
        return try {
            String.format(
                "%.2f", this, Locale.ENGLISH
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun Double?.changeDoubleToStringWith3Decimal(): String {
        return try {
            String.format(
                "%.3f", this, Locale.ENGLISH
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun String?.toCustomDouble(): Double {
        return try {
            this?.toDouble() ?: 0.0
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun findSimilarNames(list1: ArrayList<String>, list2: ArrayList<String>): List<String> {
        val similarNames = mutableListOf<String>()

        for (name1 in list1) {
            for (name2 in list2) {
                if (name1.equals(name2, ignoreCase = true)) {
                    similarNames.add(name1)
                    break  // Break inner loop since the name is already found in list2
                }
            }
        }

        return similarNames
    }
}