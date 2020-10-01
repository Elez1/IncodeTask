package com.scheduler.incodetask.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.adapter.PhotoAdapter
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.retrofit.RetrofitInstance
import com.scheduler.incodetask.service.PhotoConverterService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PhotoAdapter.OnPhotoClickedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        val PHOTO_KEY = "${Photo::class.java.simpleName}_KEY"
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var recyclerView: RecyclerView

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
                scaledBitmap = if (adapter.listOfPhotos.isNotEmpty()) {
                    val width = adapter.bitmapList[0].second.width
                    val height = adapter.bitmapList[0].second.height
                    Bitmap.createScaledBitmap(b, width, height, false)
                } else {
                    val width = b.width
                    val height = b.height
                    val aspectRatio = height / width.toDouble()
                    val newHeight = 300
                    val newWidth = (newHeight * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(b, newWidth, newHeight, false)
                }

                (recyclerView.adapter as PhotoAdapter).addPhoto(0, Pair("0", scaledBitmap!!))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getPhotos()
    }

    private fun getPhotos() {
        val retrofit = RetrofitInstance.provideRetrofit()
        val photoService = RetrofitInstance.providePhotoService(retrofit)
        val adapter = recyclerView.adapter as PhotoAdapter
        if (adapter.listOfPhotos.isNotEmpty()) {
            addPhotosToAdapter(adapter.listOfPhotos)
            return
        }
        compositeDisposable.add(
            photoService.getPhotos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    addPhotosToAdapter(it)
                    for (photo in it) {
                        Log.e("sdfsdfsdf", photo.toString())
                    }
                    adapter.listOfPhotos = it
                    //hide progress bar
                }, {
                    throw it
                })
        )

    }

    private fun addPhotosToAdapter(mutableList: MutableList<Photo>) {
        val adapter = recyclerView.adapter as PhotoAdapter
        for (photo in mutableList) {
            compositeDisposable.add(
                PhotoConverterService().getBitmapFromUrl(photo).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({
                        adapter.addPhoto(it)
                    }, {
                        throw it
                    })
            )
        }
    }

    override fun onPhotoClicked(photo: Photo) {
        startActivity(Intent(this, PhotoActivity::class.java).apply {
            putExtra(PHOTO_KEY, photo)
        })
    }

    private fun checkCameraHardware(context: Context) = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

}