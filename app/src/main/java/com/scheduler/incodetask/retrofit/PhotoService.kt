package com.scheduler.incodetask.retrofit

import com.scheduler.incodetask.model.Photo
import io.reactivex.Observable
import retrofit2.http.GET

//import java.util.*

interface PhotoService {

    @GET(value = "get/cftPFNNHsi")
    fun getPhotos(): Observable<MutableList<Photo>>
}