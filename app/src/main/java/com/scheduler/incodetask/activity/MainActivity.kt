package com.scheduler.incodetask.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.adapter.PhotoAdapter
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.service.BitmapService
import com.scheduler.incodetask.viewmodel.PhotoViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


// TODO: 10/2/20 Spinner; Layout do dugmeta za slikanje; Camera preview; Photo size

class MainActivity : BaseActivity(), PhotoAdapter.OnPhotoClickedListener, PhotoViewModel.PhotoListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        val PHOTO_KEY = "${Photo::class.java.simpleName}_KEY"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PhotoViewModel

    private val coroutineJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(coroutineJob)

    private val bitmapService = BitmapService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = PhotoViewModel(this)

        recyclerView = photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PhotoAdapter(this@MainActivity, viewModel)
        }

        takePhotoButton.setOnClickListener {
            startActivityForResult(Intent(this, CameraActivity::class.java), 9001)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001 && resultCode == Activity.RESULT_OK) {
            val path = data?.getStringExtra("sdfsdfsdf")

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
        val photoWrapperList = viewModel.getPhotos()
        withContext(Dispatchers.Main) {
            for (photoWrapper in photoWrapperList) {
                Log.e("sdfsdfsdf", "On resume listItem: $photoWrapper")
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

    // TODO: 10/4/20 delete
    override fun onPhotosReady(photoWrapper: PhotoWrapper) {
        coroutineScope.launch(Dispatchers.Main) {
            (recyclerView.adapter as PhotoAdapter).addPhoto(photoWrapper)
        }
    }

    private fun checkCameraHardware(context: Context) = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

//    override fun onStop() {
//        if (coroutineJob.isActive) {
//            coroutineJob.cancel()
//        }
//
//        super.onStop()
//    }
}