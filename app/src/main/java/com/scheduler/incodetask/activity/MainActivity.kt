package com.scheduler.incodetask.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.adapter.PhotoAdapter
import com.scheduler.incodetask.extensions.rotate
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.service.BitmapService
import com.scheduler.incodetask.viewmodel.PhotoViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream


// TODO: 10/2/20 Spinner; Layout do dugmeta za slikanje; Camera preview; Photo size

class MainActivity : AppCompatActivity(), PhotoAdapter.OnPhotoClickedListener, PhotoViewModel.PhotoListener {

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

            GlobalScope.launch {

                bitmapService.getBitmapFromPath(path!!) {
                    val resized = bitmapService.resize(it, 900, 450)
                    (recyclerView.adapter as PhotoAdapter).addPhoto(0, PhotoWrapper.createUserPhoto(resized!!, path))
                }
            }
//            val b = BitmapFactory.decodeFile(path)
//
//            GlobalScope.launch {
//                Log.e("sdfsdfsdf", "Bitmap size: ${b.toBytes().size}")
////                Log.e("sdfsdfsdf", "Compressed size: ${bitmap.toBytes().size}")
//
//                GlobalScope.launch(Dispatchers.Main) {
//                    Log.e("sdfsdfsdf", "star rotation")
//                    val bitmap = bitmapService.resize(b, 900, 450)
//                    val rotated = bitmap?.rotate(-90)
//
//                    Log.e("sdfsdfsdf", "fin rotation")
//
//                    Log.e("sdfsdfsdf", "fin resize")
//                    b.recycle()
//                    (recyclerView.adapter as PhotoAdapter).addPhoto(0, PhotoWrapper.createUserPhoto(rotated?.await()!!, path!!))
//                }
//            }

//            Handler().post {
//
//                val adapter = recyclerView.adapter as PhotoAdapter
//                val list = adapter.photoWrapperList
//                scaledBitmap = if (list.isNotEmpty()) {
//
//                    val width = list[0].bitmap!!.width
//                    val height = list[0].bitmap!!.height
//                    Bitmap.createScaledBitmap(b, width, height, false)
//                } else {
//                    val width = b.width
//                    val height = b.height
//                    val aspectRatio = height / width.toDouble()
//                    val newHeight = 300
//                    val newWidth = (newHeight * aspectRatio).toInt()
//                    Bitmap.createScaledBitmap(b, newWidth, newHeight, false)
//                }
//
//            }
        }
    }


    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)


        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    private suspend fun compressBitmap(b: Bitmap) = withContext(Dispatchers.IO) {
        val byteOutputStream = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.JPEG, 85, byteOutputStream)
        BitmapFactory.decodeByteArray(byteOutputStream.toByteArray(), 0, byteOutputStream.toByteArray().size)
    }

    override fun onResume() {
        super.onResume()

        getPhotos()
    }

    private fun getPhotos() {
        coroutineScope.launch {
            val list = viewModel.getPhotos()
            withContext(Dispatchers.Main) {
                for (pw in list) {
                    Log.e("sdfsdfsdf", "On resume listItem: $pw")
                    (recyclerView.adapter as PhotoAdapter).addPhoto(pw)
                }
            }
        }
    }

//    private fun getPhotos() {
//        coroutineScope.launch {
//            withContext(Dispatchers.Main) {
//                viewModel.getPhotos().observe(this@MainActivity, object : Observer<MutableList<PhotoWrapper>> {
//                    override fun onChanged(list: MutableList<PhotoWrapper>) {
//                        Log.e("sdfsdfsdf", "Entered on changed")
//                        val adapter = recyclerView.adapter as PhotoAdapter
////                        adapter.photoWrapperList.add(list[list.size-1])
//                        adapter.photoWrapperList = list
//                    }
//                })
//
//            }
//        }
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

//    }

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

    override fun onStop() {
        if (coroutineJob.isActive) {
            coroutineJob.cancel()
        }

        super.onStop()
    }

    override fun onPhotosReady(photoWrapper: PhotoWrapper) {
        coroutineScope.launch(Dispatchers.Main) {
            (recyclerView.adapter as PhotoAdapter).addPhoto(photoWrapper)
        }
    }
}