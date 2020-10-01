package com.scheduler.incodetask.model

import android.util.Log
import java.io.Serializable

data class Photo(var _id: String, var title: String, var comment: String, var publishedAt: String, var picture: String) : Serializable{
    fun replacePictureUrlWithId(id:String){
        picture = "https://unsplash.it/id/$id/600/300/"
        Log.e("sdfsdfsdf", "Setting new url: $picture")
    }
}