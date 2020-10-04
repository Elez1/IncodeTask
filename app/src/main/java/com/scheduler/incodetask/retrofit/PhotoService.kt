package com.scheduler.incodetask.retrofit

import com.scheduler.incodetask.model.Photo
import retrofit2.http.GET

interface PhotoService {

    @GET(value = "get/cftPFNNHsi")
    suspend fun getPhotos(): MutableList<Photo>
}