package com.scheduler.incodetask.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.adapter.PhotoAdapter
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.retrofit.PhotoService
import com.scheduler.incodetask.retrofit.RetrofitInstance
import com.scheduler.incodetask.service.PhotoConverterService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.photo_list_item.view.*

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

        if(requestCode == 9001 && resultCode == Activity.RESULT_OK){
            val photoBytes = data?.getByteArrayExtra("sdfsdfsdf")
            (recyclerView.adapter as PhotoAdapter).addPhoto(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes!!.size))
        }
    }

    override fun onResume() {
        super.onResume()

        getPhotos()
    }

    private fun getPhotos() {
        val retrofit = RetrofitInstance.provideRetrofit()
        val r = retrofit.create(PhotoService::class.java)
        val photoService = RetrofitInstance.providePhotoService(retrofit)
        compositeDisposable.add(photoService.getPhotos()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                Log.e("sdfsdfsdf", "Do on subscribe")
            }.doOnNext {
                Log.e("sdfsdfsdf", "Do on next")
            }.subscribeOn(Schedulers.newThread())
            .subscribe({
//                val listOfPhotos = getBitmaps(it)
                (recyclerView.adapter as PhotoAdapter).listOfPhotos = it
            }, {
                throw it
            })
        )

    }

    private fun getBitmaps(mutableList: MutableList<Photo>): MutableList<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()

        for (photo in mutableList){
            compositeDisposable.add(
                PhotoConverterService().getBitmapFromUrl(photo.picture)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
//                    Log.e("sdfsdsdf", "Setting photo on position $position: ${photo.picture}")
//                    listItemView.photoImageView.setImageBitmap(it)
                    bitmaps.add(it)
                }, {
                    throw it
                })
            )
        }

        return bitmaps
    }

    override fun onPhotoClicked(photo: Photo) {
        startActivity(Intent(this, PhotoActivity::class.java).apply {
            putExtra(PHOTO_KEY, photo)
        })
    }

    private fun checkCameraHardware(context: Context) =  context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
}