package com.scheduler.incodetask.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.adapter.PhotoAdapter
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.viewmodel.PhotoViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), PhotoAdapter.OnPhotoClickedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        val PHOTO_KEY = "${Photo::class.java.simpleName}_KEY"
    }

    private lateinit var recyclerView: RecyclerView

    private val coroutineJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(coroutineJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PhotoAdapter(this@MainActivity)
        }

        takePhotoButton.setOnClickListener {
            startActivityForResult(Intent(this, CameraActivity::class.java), 9001)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001 && resultCode == Activity.RESULT_OK) {
//            val photoBytes = data?.getByteArrayExtra("sdfsdfsdf")

            val path = data?.getStringExtra("sdfsdfsdf")

            val b = BitmapFactory.decodeFile(path)
//            String path = Environment.getExternalStorageDirectory() + fName + ".png"
//            Bitmap bm = BitmapFactory.decodeFile(path);
//
//            val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes!!.size)

            var scaledBitmap: Bitmap? = null
            Handler().post {

                val adapter = recyclerView.adapter as PhotoAdapter
                val list = adapter.photoWrapperList
                scaledBitmap = if (list.isNotEmpty()) {

                    val width = list[0].bitmap!!.width
                    val height = list[0].bitmap!!.height
                    Bitmap.createScaledBitmap(b, width, height, false)
                } else {
                    val width = b.width
                    val height = b.height
                    val aspectRatio = height / width.toDouble()
                    val newHeight = 300
                    val newWidth = (newHeight * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(b, newWidth, newHeight, false)
                }

                adapter.addPhoto(PhotoWrapper.createUserPhoto(scaledBitmap!!, path!!))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getPhotos()
    }

    private fun getPhotos() {
        val viewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.getPhotos().observe(this@MainActivity, object : Observer<MutableList<PhotoWrapper>> {
                    override fun onChanged(list: MutableList<PhotoWrapper>?) {
                        val adapter = recyclerView.adapter as PhotoAdapter
                        if (list == null || list.isEmpty()) return
                        adapter.photoWrapperList = list
                    }
                })
            }
        }
//        val retrofit = RetrofitInstance.provideRetrofit()
//        val photoService = RetrofitInstance.providePhotoService(retrofit)
//        val adapter = recyclerView.adapter as PhotoAdapter
//        if (adapter.listOfPhotos.isNotEmpty()) {
//            addPhotosToAdapter(adapter.listOfPhotos)
//            return
//        }
//        compositeDisposable.add(
//            photoService.getPhotos()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe({
//                    addPhotosToAdapter(it)
//                    adapter.listOfPhotos = it
//                    for (photo in it) {
//                        Log.e("sdfsdfsdf", photo.toString())
//                    }
//                    //hide progress bar
//                }, {
//                    throw it
//                })
//        )

    }

    private fun addPhotosToAdapter(mutableList: MutableList<Photo>) {
//        val adapter = recyclerView.adapter as PhotoAdapter
//        for (photo in mutableList) {
//            compositeDisposable.add(
//                PhotoConverterService().getBitmapFromUrl(photo).observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.newThread())
//                    .subscribe({
//                        adapter.addPhoto(it)
//                    }, {
//                        throw it
//                    })
//            )
//        }
    }

    override fun onPhotoClicked(photoWrapper: PhotoWrapper) {
        startActivity(Intent(this, PhotoActivity::class.java).apply {
            putExtra(PHOTO_KEY, photoWrapper.photo)
        })
    }

    private fun checkCameraHardware(context: Context) = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    override fun onPause() {
        if (coroutineJob.isActive){
            coroutineJob.cancel()
        }

        super.onPause()
    }
}