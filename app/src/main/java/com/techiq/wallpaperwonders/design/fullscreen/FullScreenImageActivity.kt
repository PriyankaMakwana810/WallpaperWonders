package com.techiq.wallpaperwonders.design.fullscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.ResultReceiver
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.base.ActivityBase
import com.techiq.wallpaperwonders.databinding.ActivityFullScreenImageBinding
import com.techiq.wallpaperwonders.design.main.MainViewModel
import com.techiq.wallpaperwonders.utils.Constants
import java.io.File
import java.util.Objects

class FullScreenImageActivity : ActivityBase(), View.OnClickListener {
    val binding: ActivityFullScreenImageBinding by binding(R.layout.activity_full_screen_image)
    private val viewModelMain by viewModels<MainViewModel>()
    private var previewImageLink: String? = null
    private var largeImageLink: String? = null
    private var imageId: String? = null
    private var poweredBy = 0
    var isImageDownloaded = false
    var isClickedShare = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@FullScreenImageActivity
//            setObservers()
            viewModelMain.parentView.set(frameMain)
        }
        if (intent.extras != null) {
            previewImageLink = intent.getStringExtra(Constants.KEY_PREVIEW_IMAGE_LINK)
            largeImageLink = intent.getStringExtra(Constants.KEY_LARGE_IMAGE_LINK)
            imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)
            isImageDownloaded = intent.getBooleanExtra(Constants.KEY_IMAGE_DOWNLOAD, false)
            if (!intent.extras?.containsKey(Constants.FROM)!!) {
                imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)
                if (checkIfFileAlreadyExists()) {
                    largeImageLink = imageFilePath
                }
            }
        }
        setUpToolbar()
        val w: Window = window // in Activity's onCreate() for instance
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding.toolbar.setPadding(0, 10, 0, 0)
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = ""
        binding.ivBack.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
    }


    private fun checkIfFileAlreadyExists(): Boolean {
        val file = File(
            Objects.requireNonNull(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).absolutePath +
                    File.separator + getString(R.string.app_name) + File.separator + imageId + ".jpg"
        )
        return file.exists()
    }

    private val imageFilePath: String
        get() = Objects.requireNonNull(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        ).absolutePath +
                File.separator + getString(R.string.app_name) + File.separator + imageId + ".jpg"


    override fun onClick(v: View?) {
        when (v) {
            binding.ivBack -> {
                finish()
            }

            binding.ivShare -> {
                if (checkIfFileAlreadyExists()) {
                    shareImage(imageFilePath)
                } else {
                    checkPermissions()
                }
            }
        }
    }

    private fun checkPermissions() {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            if (Build.VERSION.SDK_INT <= 29) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 11
                )
            } else {
                handleDownload()
            }
        } else {
            handleDownload()
        }
    }

    private fun handleDownload() {
        if (!checkIfFileAlreadyExists()) {
            downloadImage()
        } else {
            Toast.makeText(
                this@FullScreenImageActivity,
                "This wallpaper is already downloaded",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun downloadImage() {

    }

    private fun shareImage(path: String?) {
        var sAux =
            "\nHi, I found this amazing app for HD Wallpapers, Give it a try its free and easy to use\n\n"
        sAux =
            """${sAux + "https://play.google.com/store/apps/details?id=" + applicationContext.packageName} """
        val share = Intent(Intent.ACTION_SEND)
        val photoURI: Uri =
            FileProvider.getUriForFile(this, "com.techiq.wallpaperwonders.provider", File(path!!))
        share.putExtra(Intent.EXTRA_STREAM, photoURI)
        share.putExtra(Intent.EXTRA_TEXT, sAux)
        share.type = "image/*"
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(share, "Share via"))
    }

    private inner class DownloadReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
//            if (resultCode == DownloadService.UPDATE_PROGRESS) {
//                val progress: Int = resultData.getInt("progress")
////                mProgressDialog!!.progress = progress
//                if (progress == 100) {
////                    mProgressDialog!!.dismiss()
//                    if (isClickedShare) {
//                        shareImage(imageFilePath)
//                        isClickedShare = false
//                    }
//                }
//            }
        }
    }

}