package com.scheduler.incodetask.repository

import android.graphics.BitmapFactory
import com.scheduler.incodetask.retrofit.PhotoService
import com.scheduler.incodetask.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import javax.inject.Inject

class PhotoRepository @Inject constructor(private var photoService: PhotoService) {

//    //todo add dagger
//    val retrofit = RetrofitInstance.provideRetrofit()
//    val photoService = RetrofitInstance.providePhotoService(retrofit)

    suspend fun getPhotos() = photoService.getPhotos()

    fun getBitmapFromUrlAsync(urlString: String) = GlobalScope.async(Dispatchers.IO) {
        BitmapFactory.decodeStream(URL(urlString).openConnection().getInputStream()) ?: throw Exception("Bitmap can't be null")
    }

    fun getPhotoIdAndBitmapPair(photoUrl: String) = GlobalScope.async(Dispatchers.IO) {
        val url = URL(photoUrl)
        val conn: URLConnection = url.openConnection()
        conn.connect()
        val inputS: InputStream = conn.getInputStream()
        val redirectedUrl = conn.url

        val splitPath = redirectedUrl.path.split("/")
        val bitmap = BitmapFactory.decodeStream(inputS)
        inputS.close()
        Pair(splitPath[2], bitmap)
    }
}