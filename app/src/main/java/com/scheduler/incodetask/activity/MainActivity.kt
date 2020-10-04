package com.scheduler.incodetask.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.adapter.PhotoAdapter
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.service.BitmapService
import com.scheduler.incodetask.handler.PhotoHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : BaseActivity(), PhotoAdapter.OnPhotoClickedListener, PhotoHandler.PhotoListener {

    companion object {
        val PHOTO_KEY = "${Photo::class.java.simpleName}_KEY"
        val PHOTO_PATH_KEY = "${Photo::class.java.simpleName}_PATH_KEY"
    }

    private val TAG = MainActivity::class.java.simpleName
    private val TAKE_PHOTO_REQUEST_CODE = 9001

    private lateinit var recyclerView: RecyclerView
    private lateinit var handler: PhotoHandler

    private val bitmapService = BitmapService()

    private val coroutineJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(coroutineJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = PhotoHandler(this)

        recyclerView = photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PhotoAdapter(this@MainActivity)
        }

        takePhotoButton.setOnClickListener {
            if (cameraExists(this)) {
                startActivityForResult(Intent(this, CameraActivity::class.java), TAKE_PHOTO_REQUEST_CODE)
            } else {
                showInfoDialog()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val path = data?.getStringExtra(PHOTO_PATH_KEY)

            coroutineScope.launch {
                bitmapService.getBitmapFromPath(path!!) {
                    val resized = bitmapService.resize(it, 900, 450)
                    (recyclerView.adapter as PhotoAdapter).addPhoto(0, PhotoWrapper.createUserPhoto(resized!!, path))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getPhotos()
    }

    private fun getPhotos() = coroutineScope.launch {
        val adapter = recyclerView.adapter as PhotoAdapter
        if (adapter.photoWrapperList.isNotEmpty()) {
            return@launch
        }
        showSpinner()
        val photoWrapperList = handler.getPhotos()
        withContext(Dispatchers.Main) {
            for (photoWrapper in photoWrapperList) {
                Log.d(TAG, "On resume listItem: $photoWrapper")
                adapter.addPhoto(photoWrapper)
            }
            hideSpinner()
        }
    }

    override fun onPhotoClicked(photoWrapper: PhotoWrapper) {
        startActivity(Intent(this, PhotoActivity::class.java).apply {
            putExtra(PHOTO_KEY, photoWrapper.photo)
        })
    }

    // used for adding photos one by one
    // currently not used, but left as an option
    override fun onPhotosReady(photoWrapper: PhotoWrapper) {
        coroutineScope.launch(Dispatchers.Main) {
            (recyclerView.adapter as PhotoAdapter).addPhoto(photoWrapper)
        }
    }

    private fun cameraExists(context: Context) = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle("This device doesn't have a camera")
            .setPositiveButton(android.R.string.yes, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}