package com.scheduler.incodetask.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

class PhotoViewModel() : ViewModel() {

    val retrofit = RetrofitInstance.provideRetrofit()
    val photoService = RetrofitInstance.providePhotoService(retrofit)

    var photoWrapperList = MutableLiveData<MutableList<PhotoWrapper>>()

    suspend fun getPhotos(): MutableLiveData<MutableList<PhotoWrapper>> {
        withContext(Dispatchers.IO) {
            val photos = photoService.getPhotos()
            val photoWrappers = mutableListOf<PhotoWrapper>()
            for (photo in photos) {
                val photoWrapper = getBitmapAndId(photo)
                photoWrappers.add(photoWrapper!!)
            }
            withContext(Dispatchers.Main) {
                photoWrapperList.value = photoWrappers
            }
        }

        return photoWrapperList
    }

    private fun getBitmapAndId(photo: Photo): PhotoWrapper? {
        val url = URL(photo.picture)
        val conn: URLConnection = url.openConnection()
        conn.connect()
        val inputS: InputStream = conn.getInputStream()
        val redirectedUrl = conn.url
        Log.e("sdfsdfsdf", "redirected url:  $redirectedUrl")
        inputS.close()

        val splitPath = redirectedUrl.path.split("/")
        val photoId = splitPath[2]
        photo.replacePictureUrlWithId(photoId)
        // TODO: 10/1/20 ovde negde je greska

        val bitmap = BitmapFactory.decodeStream(URL(photo.picture).openConnection().getInputStream()) ?: throw Exception("Bitmap can't be null")
        return PhotoWrapper(photoId, photo, bitmap)
    }

    interface PhotoListener {
        fun onPhotosReady(photoWrapperList: MutableList<PhotoWrapper>)
    }

}