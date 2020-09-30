package com.scheduler.incodetask.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.io.IOException
import java.lang.Exception
import java.net.URL


class PhotoConverterService {

    fun getBitmapFromUrl(urlString: String): Observable<Bitmap> = try {
        Observable.create(ObservableOnSubscribe<Bitmap> {
            val bitmap = BitmapFactory.decodeStream(URL(urlString).openConnection().getInputStream())
            if(bitmap == null){
                it.onError(Exception("Image is null"))
                return@ObservableOnSubscribe
            }

            it.onNext(bitmap)
            it.onComplete()
        })

    } catch (e: IOException) {
        throw e
    }

}