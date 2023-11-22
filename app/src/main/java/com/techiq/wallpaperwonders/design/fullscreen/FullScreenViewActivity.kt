package com.techiq.wallpaperwonders.design.fullscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.base.ActivityBase
import com.techiq.wallpaperwonders.databinding.ActivityFullScreenViewBinding
import com.techiq.wallpaperwonders.databinding.DialogWallpaperTypeChooserBinding
import com.techiq.wallpaperwonders.service.DownloadService
import com.techiq.wallpaperwonders.service.VideoLiveWallpaperService
import com.techiq.wallpaperwonders.utils.Constant.smallToast
import com.techiq.wallpaperwonders.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.Objects

@AndroidEntryPoint
class FullScreenViewActivity : ActivityBase(), View.OnClickListener {

    // View binding
    val binding: ActivityFullScreenViewBinding by binding(R.layout.activity_full_screen_view)

    // Variables for image details and download status
    private var previewImageLink: String? = null
    private var largeImageLink: String? = null
    private var imageId: String? = null

    private var videoLink: String? = null
    private var videoId: String? = null

    var isImageDownloaded = false
    var isVideoDownloaded = false

    var isClickedShare = false

    private var progressDialog: AlertDialog? = null
    private var progressBar: ProgressBar? = null

    private var isWallpaperReadyToSet = false
    var lastShownToastTime: Long = 0

    var height = 0
    var width = 0

    var isHomeClicked = false
    var isLockScreenClicked = false
    private var poweredBy = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@FullScreenViewActivity
        }

        // Retrieve image details from intent
        if (intent.extras != null) {
            previewImageLink = intent.getStringExtra(Constants.KEY_PREVIEW_IMAGE_LINK)
            largeImageLink = intent.getStringExtra(Constants.KEY_LARGE_IMAGE_LINK)
            imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)
            isImageDownloaded = intent.getBooleanExtra(Constants.KEY_IMAGE_DOWNLOAD, false)

            videoLink = intent.getStringExtra(Constants.KEY_VIDEO_LINK)
            videoId = intent.getStringExtra(Constants.KEY_VIDEO_ID)
            isVideoDownloaded = intent.getBooleanExtra(Constants.KEY_VIDEO_DOWNLOAD, false)

            // Additional logic for file existence check
            if (!intent.extras?.containsKey(Constants.FROM)!!) {
                imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)
                videoId = intent.getStringExtra(Constants.KEY_VIDEO_ID)

                if (checkIfFileAlreadyExists()) {
                    largeImageLink = imageFilePath
                }

                if (checkIfVideoFileAlreadyExists()) {
                    videoLink = videoFilePath
                }
            }
        }

        // Set up the toolbar and adjust system UI
        setUpToolbar()
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (hasNavBar()) {
            binding.frameMain.setPadding(0, 0, 0, navigationBarHeight)
        }
        // Prepare the layout
        prepareLayout()
    }

    // Function to set up the toolbar
    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = ""
        binding.ivBack.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
    }

    // Function to prepare the layout
    private fun prepareLayout() {
        binding.progressBar.visibility = View.VISIBLE
        loadLargeImage()
        binding.ivImage.setOnClickListener(this)
        binding.ivVideo.setOnClickListener(this)
        binding.btnSet.setOnClickListener(this)
        binding.btnDownload.setOnClickListener(this)
        if (hasNavBar()) {
            binding.frameMain.setPadding(0, 0, 0, navigationBarHeight)
        }
        progressDialog = createProgressDialog()
        if (isImageDownloaded || isVideoDownloaded) {
            binding.btnDownload.visibility = View.GONE
            binding.btnDownload.setText(R.string.delete)
            binding.btnDownload.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    // Function to check if the navigation bar is present
    @SuppressLint("DiscouragedApi")
    private fun hasNavBar(): Boolean {
        val id: Int = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    // Getter function for navigation bar height
    private val navigationBarHeight: Int
        get() {
            val resourceId: Int =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }

    // Function to create a progress dialog
    private fun createProgressDialog(): AlertDialog {
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog_layout, null)
        progressBar = dialogView.findViewById(R.id.progressBar)

        return AlertDialog.Builder(this@FullScreenViewActivity)
            .setView(dialogView)
            .setCancelable(false)
            .create()
    }

    // Function to load the large image
    private fun loadLargeImage() {
        poweredBy = sharedPref.getInt(Constants.POWERED_BY)
        if (poweredBy == Constants.POWERED_BY_PEXELS) {
            if (videoLink != null) {
                binding.ivVideo.visibility = View.VISIBLE
                binding.ivImage.visibility = View.GONE
                try {
                    binding.ivVideo.setVideoURI(Uri.parse(videoLink))
                    binding.ivVideo.requestFocus()
                    binding.ivVideo.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("TAG", "loadLargeImage: $e ")
                    smallToast(e.message.toString())
                }
                binding.ivVideo.setOnPreparedListener { mediaPlayer ->
                    binding.progressBar.visibility = View.GONE
                    mediaPlayer.setVolume(0f, 0f)
                    mediaPlayer.isLooping = true
                    val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
                    val screenRatio = binding.ivVideo.width / binding.ivVideo.height.toFloat()
                    val scaleX = videoRatio / screenRatio
                    if (scaleX >= 1f) {
                        binding.ivVideo.scaleX = scaleX
                    } else {
                        binding.ivVideo.scaleY = 1f / scaleX
                    }
                }
                isWallpaperReadyToSet = true
                binding.ivShare.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.ivVideo.visibility = View.GONE
                binding.ivImage.visibility = View.VISIBLE
                glideUtils.loadImage(largeImageLink, binding.ivImage)
                isWallpaperReadyToSet = true
                binding.ivShare.visibility = View.VISIBLE
            }
        } else {
            binding.progressBar.visibility = View.GONE
            binding.ivVideo.visibility = View.GONE
            binding.ivImage.visibility = View.VISIBLE
            glideUtils.loadImage(largeImageLink, binding.ivImage)
            isWallpaperReadyToSet = true
            binding.ivShare.visibility = View.VISIBLE
        }

    }

    // Function to check if the photo already exists
    private fun checkIfFileAlreadyExists(): Boolean {
        val file = File(
            Objects.requireNonNull(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).absolutePath +
                    File.separator + getString(R.string.app_name) + File.separator + imageId + ".jpg"
        )
        return file.exists()
    }

    // Function to check if the video file already exists
    private fun checkIfVideoFileAlreadyExists(): Boolean {
        val file = File(
            Objects.requireNonNull(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            ).absolutePath +
                    File.separator + getString(R.string.app_name) + File.separator + videoId + ".mp4"
        )
        return file.exists()
    }

    // Getter function for the image file path
    private val imageFilePath: String
        get() = Objects.requireNonNull(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        ).absolutePath +
                File.separator + getString(R.string.app_name) + File.separator + imageId + ".jpg"

    // Getter function for the video file path
    private val videoFilePath: String
        get() = Objects.requireNonNull(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        ).absolutePath +
                File.separator + getString(R.string.app_name) + File.separator + videoId + ".mp4"


    // Function to toggle immersive mode
    private fun toggleHideyBar() {
        val uiOptions: Int = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        if (isImmersiveModeEnabled) {
            binding.toolbar.visibility = View.VISIBLE
            binding.btnSet.visibility = View.VISIBLE
            binding.btnDownload.visibility = View.VISIBLE
        } else {
            binding.toolbar.visibility = View.GONE
            binding.btnSet.visibility = View.GONE
            binding.btnDownload.visibility = View.GONE
        }
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = newUiOptions
    }

    // OnClickListener implementation
    override fun onClick(v: View?) {
        when (v) {
            binding.ivImage -> toggleHideyBar()
            binding.ivVideo -> toggleHideyBar()

            binding.ivBack -> {
                finish()
            }

            binding.ivShare -> {
                if (videoLink == null) {
                    if (checkIfFileAlreadyExists()) {
                        shareImage(imageFilePath)
                    } else {
                        isClickedShare = true
                        checkPermissions()
                    }
                } else {
                    if (checkIfVideoFileAlreadyExists()) {
                        shareVideo(videoFilePath)
                    } else {
                        isClickedShare = true
                        checkPermissions()
                    }
                }
            }

            binding.btnSet -> {
                if (videoLink != null) {
                    if (checkIfVideoFileAlreadyExists()) {
                        val videoUri = Uri.parse(videoFilePath)
                        saveVideoAsLiveWallpaper(videoUri)
                    } else {
                        smallToast("Download First to set wallpaper!!")
                    }
                } else {
                    if (isWallpaperReadyToSet) openWallpaperTypeChooserDialog()
                }
            }

            binding.btnDownload -> {
                if (isImageDownloaded || isVideoDownloaded) {
                    if (checkStoragePermissions()) {
                        if (isImageDownloaded) {
                            largeImageLink = imageFilePath
                            val file = File(largeImageLink.toString())
                            if (file.exists()) {
                                try {
                                    Log.e("TAG", "onClick: $file")
                                    if (file.delete()) {
                                        smallToast("Wallpaper deleted successfully")
                                    } else {
                                        smallToast("Wallpaper can't delete.")
                                    }
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                    Log.e("TAG", "onClick: ${exception.message}")
                                }
                                setResult(101)
                                finish()
                            }
                        } else {
                            videoLink = videoFilePath
                            val file = File(videoLink.toString())
                            if (file.exists()) {
                                try {
                                    Log.e("TAG", "onClick: $file")
                                    if (file.delete()) {
                                        smallToast("Wallpaper deleted successfully")
                                    } else {
                                        smallToast("Wallpaper can't delete.")
                                    }
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                    Log.e("TAG", "onClick: ${exception.message}")
                                }
                                setResult(101)
                                finish()
                            }
                        }
                    } else {
                        requestForStoragePermissions()
                    }
                } else {
                    checkPermissions()
                }
            }
        }
    }

    private fun saveVideoAsLiveWallpaper(videoUri: Uri) {

        sharedPref.putString(Constants.PREF_VIDEO_URI, videoUri.toString())
        val intent = Intent(this, VideoLiveWallpaperService::class.java)
        startService(intent)

        try {
            val wallpaperManager = WallpaperManager.getInstance(this)
            wallpaperManager.clearWallpaper()
            val wallpaperInfo = wallpaperManager.wallpaperInfo
            if (wallpaperInfo != null && wallpaperInfo.packageName == packageName) {
                setResult(Activity.RESULT_OK)
            } else {
                val wallpaperIntent = Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
                )
                wallpaperIntent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this, VideoLiveWallpaperService::class.java)
                )
                startActivity(wallpaperIntent)
            }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun openWallpaperTypeChooserDialog() {
        val dialog: Dialog = BottomSheetDialog(this)
        val chooseWallpaperTypeBinding: DialogWallpaperTypeChooserBinding =
            DialogWallpaperTypeChooserBinding.inflate(layoutInflater)
        dialog.setContentView(chooseWallpaperTypeBinding.root)
        dialog.setCancelable(true)
        dialog.show()
        val displayMetrics = DisplayMetrics()
        val display: Display? = windowManager?.defaultDisplay

        if (display != null) {
            display.getMetrics(displayMetrics)
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }
        chooseWallpaperTypeBinding.tvPortraitDimension.text = java.lang.String.format(
            getString(R.string.full_hd_s),
            width.toString() + "x" + height + "px"
        )
        chooseWallpaperTypeBinding.tvLockDimension.text = java.lang.String.format(
            getString(R.string.full_hd_s),
            width.toString() + "x" + height + "px"
        )
        chooseWallpaperTypeBinding.llHomeLock.setOnClickListener {
            dialog.dismiss()
            isLockScreenClicked = true
            isHomeClicked = true
            setWallpaper()
        }
        chooseWallpaperTypeBinding.llHome.setOnClickListener {
            dialog.dismiss()
            isLockScreenClicked = false
            isHomeClicked = true
            setWallpaper()
        }
        chooseWallpaperTypeBinding.llLock.setOnClickListener {
            dialog.dismiss()
            isLockScreenClicked = true
            isHomeClicked = false
            setWallpaper()
        }
    }

    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            //Below android 11
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestForStoragePermissions() {
        //Below android 11
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            11
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 11) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else {
                // Permission is denied
                Toast.makeText(
                    this,
                    "Permission not Granted!!.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setWallpaper() {
        if (isWallpaperReadyToSet) {
            val wallpaperManager = WallpaperManager.getInstance(this)
            try {
                val bitmap = (binding.ivImage.drawable as BitmapDrawable).bitmap
                if (isHomeClicked && isLockScreenClicked) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                } else if (isLockScreenClicked) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                } else {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                }
//                wallpaperManager.setBitmap(bitmap)
                smallToast("Wallpaper set successfully")
            } catch (e: IOException) {
                e.printStackTrace()
                smallToast("Failed to set wallpaper")
            }
        }
    }

    // Function to check and request necessary permissions
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

    // Function to handle the download process
    private fun handleDownload() {
        if (videoId == null) {
            if (!checkIfFileAlreadyExists()) {
                progressDialog?.show()
                val intent = Intent(this@FullScreenViewActivity, DownloadService::class.java)
                intent.putExtra("url", largeImageLink)
                intent.putExtra(Constants.KEY_IMAGE_ID, imageId)
                intent.putExtra("receiver", DownloadReceiver(Handler(Looper.getMainLooper())))
                startService(intent)
            } else {
                smallToast("This wallpaper is already downloaded")
            }
        } else {
            if (!checkIfVideoFileAlreadyExists()) {
                progressDialog?.show()
                val intent = Intent(this@FullScreenViewActivity, DownloadService::class.java)
                intent.putExtra("url", videoLink)
                intent.putExtra(Constants.KEY_VIDEO_ID, videoId)
                intent.putExtra("receiver", DownloadReceiver(Handler(Looper.getMainLooper())))
                startService(intent)
            } else {
                smallToast("This wallpaper is already downloaded")
            }
        }
        if (!checkIfFileAlreadyExists()) {
            progressDialog?.show()
            val intent = Intent(this@FullScreenViewActivity, DownloadService::class.java)
            intent.putExtra("url", largeImageLink)
            intent.putExtra(Constants.KEY_IMAGE_ID, imageId)
            intent.putExtra("receiver", DownloadReceiver(Handler(Looper.getMainLooper())))
            startService(intent)
        } else {
            smallToast("This wallpaper is already downloaded")
        }
    }

    // Function to share the image
    private fun shareImage(path: String?) {
        var sAux =
            "\nHi, I found this amazing app for HD Wallpapers, Give it a try its free and easy to use\n\n"
        sAux =
            """${sAux + "https://play.google.com/store/apps/details?id=" + applicationContext.packageName} """
        val share = Intent(Intent.ACTION_SEND)
        val photoURI: Uri =
            FileProvider.getUriForFile(
                this,
                "com.techiq.wallpaperwonders.provider",
                File(path!!)
            )
        share.putExtra(Intent.EXTRA_STREAM, photoURI)
        share.putExtra(Intent.EXTRA_TEXT, sAux)
        share.type = "image/*"
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(share, "Share via"))
    }

    // Function to share the video
    private fun shareVideo(path: String?) {
        val sAux =
            "\nHi, I found this amazing app for HD Wallpapers, Give it a try it's free and easy to use\n\n" +
                    "https://play.google.com/store/apps/details?id=${applicationContext.packageName} "

        val share = Intent(Intent.ACTION_SEND)
        val videoURI: Uri =
            FileProvider.getUriForFile(this, "com.techiq.wallpaperwonders.provider", File(path!!))
        share.putExtra(Intent.EXTRA_STREAM, videoURI)
        share.putExtra(Intent.EXTRA_TEXT, sAux)
        share.type = "video/*"
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(share, "Share via"))

    }

    override fun onPause() {
        super.onPause()
        if (videoLink != null) {
            Log.e("onPause: ", "on pause video called")
            binding.ivVideo.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (videoLink != null) {
            Log.e("onResume: ", "on resume video called")
            binding.ivVideo.start()
        }
    }

    // ResultReceiver implementation for tracking download progress
    private inner class DownloadReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                val progress: Int = resultData.getInt("progress")
                progressBar!!.progress = progress
                if (progress == 100) {
                    progressDialog!!.dismiss()
                    if (System.currentTimeMillis() - lastShownToastTime > 5000) {
                        smallToast("Wallpaper downloaded successfully")
                        lastShownToastTime = System.currentTimeMillis()
                    }
                    if (isClickedShare) {
                        shareImage(imageFilePath)
                        isClickedShare = false
                    }
                }
            }
        }
    }
}
