package com.techiq.wallpaperwonders.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.os.ResultReceiver
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.utils.Constants
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.Objects

class DownloadService : IntentService("DownloadService") {
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val output: OutputStream
        val urlToDownload = intent!!.getStringExtra("url")
        val imageID = intent.getStringExtra(Constants.KEY_IMAGE_ID)
        val videoID = intent.getStringExtra(Constants.KEY_VIDEO_ID)
        val receiver = intent.getParcelableExtra<Parcelable>("receiver") as ResultReceiver?
        try {
            val url = URL(urlToDownload)
            val connection = url.openConnection()
            connection.connect()
            // this will be useful so that you can show a typical 0-100% progress bar
            val fileLength = connection.contentLength

            // download the file
            val input: InputStream = BufferedInputStream(connection.getInputStream())
            val folder = File(
                Objects.requireNonNull(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                ).absolutePath +
                        File.separator + getString(R.string.app_name)
            )

            if (!folder.exists()) {
                folder.mkdirs()
            }
            if (videoID == null) {
                output = FileOutputStream(folder.toString() + File.separator + imageID + ".jpg")
            } else {
                output = FileOutputStream(folder.toString() + File.separator + videoID + ".mp4")
            }


            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                // publishing the progress....
                val resultData = Bundle()
                resultData.putInt("progress", (total * 100 / fileLength).toInt())
                receiver!!.send(UPDATE_PROGRESS, resultData)
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val resultData = Bundle()
        resultData.putInt("progress", 100)
        receiver!!.send(UPDATE_PROGRESS, resultData)
    }

    companion object {
        const val UPDATE_PROGRESS = 8344
    }
}