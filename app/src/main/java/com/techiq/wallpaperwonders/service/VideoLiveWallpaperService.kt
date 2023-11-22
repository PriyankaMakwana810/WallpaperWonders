package com.techiq.wallpaperwonders.service

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.techiq.wallpaperwonders.utils.Constants
import com.techiq.wallpaperwonders.utils.PrefUtils

class VideoLiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return VideoWallpaperEngine()
    }

    inner class VideoWallpaperEngine : Engine(), SurfaceHolder.Callback,
        SharedPreferences.OnSharedPreferenceChangeListener {
        private lateinit var mediaPlayer: MediaPlayer
        private lateinit var surfaceHolder: SurfaceHolder
        private var videoUri: Uri? = null
        private var sharedPref: PrefUtils? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            this.surfaceHolder = surfaceHolder
            surfaceHolder.addCallback(this)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            sharedPref = PrefUtils(applicationContext)
            val videoUriString = sharedPref?.getString(Constants.PREF_VIDEO_URI)
            videoUri = Uri.parse(videoUriString)

            mediaPlayer = MediaPlayer()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
            videoUri?.let { startVideo(it) }
        }

        private fun startVideo(videoUri: Uri) {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(applicationContext, videoUri)
                mediaPlayer.setSurface(surfaceHolder.surface)
                mediaPlayer.setVolume(0f, 0f)
                mediaPlayer.isLooping = true
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            videoUri?.let { startVideo(it) }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            // No implementation needed
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mediaPlayer.release()
        }

        override fun onDestroy() {
            super.onDestroy()
            surfaceHolder.removeCallback(this)
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?,
        ) {
            if (key == Constants.PREF_VIDEO_URI) {
                val videoUriString = sharedPreferences?.getString(Constants.PREF_VIDEO_URI, "")
                videoUri = Uri.parse(videoUriString)
                videoUri?.let { startVideo(it) }
            }
        }
    }
}
