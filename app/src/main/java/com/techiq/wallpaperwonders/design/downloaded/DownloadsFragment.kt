package com.techiq.wallpaperwonders.design.downloaded

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.adapters.DownloadedWallpapersAdapter
import com.techiq.wallpaperwonders.base.BaseFragment
import com.techiq.wallpaperwonders.databinding.FragmentDownloadedBinding
import com.techiq.wallpaperwonders.design.fullscreen.FullScreenViewActivity
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Arrays
import java.util.Objects


@AndroidEntryPoint
class DownloadsFragment : BaseFragment() {

    lateinit var mBinder: FragmentDownloadedBinding
    var adapter: DownloadedWallpapersAdapter? = null
    var files: Array<File>? = null
    private var permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        mBinder = FragmentDownloadedBinding.inflate(inflater, container, false)
        prepareLayout()
        return mBinder.root
    }

    // Function to recursively delete files and directories
    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteRecursive(
            child
        )
        if (fileOrDirectory.delete()) {
            Constant.smallToastWithContext(requireContext(), "Deleted Successfully.")
        }
        fileOrDirectory.delete()
        setData()
    }

    // Function to set up the layout
    private fun prepareLayout() {
        if (hasPermissions(requireContext(), permissions)) {
            setData()
        } else {
            permReqLauncher.launch(permissions)
        }
    }

    // Permission request launcher
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }

            if (granted) {
                setData()
            } else {
                showPermissionSettingsDialog()
            }
        }

    // Function to check if permissions are granted
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun showPermissionSettingsDialog() {
        val message: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                message = "Please grant Photos & Videos  Permission from App Settings!!"
            } else {
                message = "Please grant Photos & Videos Permission from App Settings!!"
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                message = "Please grant Storage Permission from App Settings!!"
            } else {
                message = "Please grant Storage Permission from App Settings!!"
            }
        }
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Permissions Required")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("Settings") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)

        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }

    private fun setUpRecyclerView() {
        if (files != null && files!!.isNotEmpty()) {
            mBinder.rvImages.visibility = View.VISIBLE
            mBinder.llError.visibility = View.GONE
            mBinder.rvImages.layoutManager = GridLayoutManager(mActivity, 3)
            adapter = DownloadedWallpapersAdapter(glideUtils, files!!)
            mBinder.rvImages.adapter = adapter
            adapter!!.setOnItemClickListener(object : OnItemClickedListener {
                override fun onItemClicked(position: Int) {
                    val extension =
                        files!![position].path.substring(files!![position].path.lastIndexOf("."))
                    val id1 = files!![position].path.substring(
                        files!![position].path.lastIndexOf(
                            File.separator
                        ), files!![position].path.lastIndexOf(".")
                    )
                    val mediaId = id1.substring(1)
                    val intent = Intent(mActivity, FullScreenViewActivity::class.java)
                    if (extension == ".mp4") {
                        intent.putExtra(Constants.KEY_VIDEO_LINK, files!![position].path)
                        intent.putExtra(Constants.KEY_VIDEO_ID, mediaId)
                        intent.putExtra(Constants.KEY_VIDEO_DOWNLOAD, true)
                    } else {
                        intent.putExtra(Constants.KEY_PREVIEW_IMAGE_LINK, files!![position].path)
                        intent.putExtra(Constants.KEY_LARGE_IMAGE_LINK, files!![position].path)
                        intent.putExtra(Constants.KEY_IMAGE_ID, mediaId)
                        intent.putExtra(Constants.KEY_IMAGE_DOWNLOAD, true)
                    }
                    activityResultLauncher.launch(intent)
                }

                override fun onSubItemClicked(position: Int, subPosition: Int) {
                }
            })
        } else {
            mBinder.rvImages.visibility = View.GONE
            mBinder.llError.visibility = View.VISIBLE
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            setData()
        }
    }

    private fun setData() {
        val path =
            Objects.requireNonNull(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).absolutePath + File.separator + getString(
                R.string.app_name
            )
        Log.e("Files", "Path: $path")
        val directory = File(path)
        files = directory.listFiles()
        if (files != null) {
            files?.let {
                Arrays.sort(it) { o1, o2 ->
                    if ((o1 as File).lastModified() > (o2 as File).lastModified()) {
                        -1
                    } else if (o1.lastModified() < o2.lastModified()) {
                        +1
                    } else {
                        0
                    }
                }
            }
        }
        setUpRecyclerView()
    }
}