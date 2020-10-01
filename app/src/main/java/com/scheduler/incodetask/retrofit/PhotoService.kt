package com.scheduler.incodetask.retrofit

import androidx.lifecycle.LiveData
import com.scheduler.incodetask.model.Photo
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface PhotoService {

    @GET(value = "get/cftPFNNHsi")
    suspend fun getPhotos(): MutableList<Photo>
}