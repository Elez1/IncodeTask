package com.scheduler.incodetask.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.scheduler.incodetask.model.Photo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class PhotoConverterService {

    fun getBitmapFromUrl(photo: Photo): Observable<Bitmap> = try {
        Observable.create(ObservableOnSubscribe<Bitmap> {
            val url = URL(photo.picture)
            val conn: URLConnection = url.openConnection()
//            Log.e( "sdfsdfsdf","orignal url: " + conn.getURL())
            conn.connect()
//            Log.e("sdfsdfsdf","connected url: " + conn.getURL())
            val inputS: InputStream = conn.getInputStream()
            val redirectedUrl = conn.url
            Log.e("sdfsdfsdf", "redirected url:  $redirectedUrl")
            inputS.close()

//            Log.e("sdfsddfsdf", "Url path: ${redirectedUrl.path}")
            val splitPath = redirectedUrl.path.split("/")
            photo.replacePictureUrlWithId(splitPath[2])
            // TODO: 10/1/20 ovde negde je greska
//            Log.e("sdfsdfsdf", "Photo id: ${splitPath[2]}")


            val bitmap = BitmapFactory.decodeStream(URL(photo.picture).openConnection().getInputStream())
            if (bitmap == null) {
                it.onError(Exception("Image is null"))
                return@ObservableOnSubscribe
            }
            it.onNext(bitmap)
            it.onComplete()
        })

    } catch (e: IOException) {
        throw e
    }

    fun getBitmapsFromUrl(photoList: MutableList<Photo>): Observable<MutableList<Bitmap>> {
        return Observable.create(ObservableOnSubscribe<MutableList<Bitmap>> {
            val list = mutableListOf<Bitmap>()
            for (photo in photoList) {
                val url = URL(photo.picture)
                val conn: URLConnection = url.openConnection()
                conn.connect()
                val redirectedUrl = conn.url
                val splitPath = redirectedUrl.path.split("/")
                Log.e("sdfsdfsdf", "Photo id: ${splitPath[2]}")
                photo.replacePictureUrlWithId(splitPath[2])

                val bitmap = BitmapFactory.decodeStream(conn.getInputStream())
                if (bitmap != null) {
                    list.add(bitmap)
                }
            }
            it.onNext(list)
            it.onComplete()
//        return list

        })
    }

}