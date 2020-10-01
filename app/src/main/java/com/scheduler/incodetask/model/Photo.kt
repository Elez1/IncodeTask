package com.scheduler.incodetask.model

import android.util.Log
import java.io.Serializable
import java.net.URL

data class Photo(var _id: String, var title: String, var comment: String, var publishedAt: String, var picture: String) : Serializable{
    fun replacePictureUrlWithId(id:String){
        picture = "https://unsplash.it/id/$id/600/300/"
        Log.e("sdfsdfsdf", "Setting new url: $picture")
    }

    fun getPhotoId(): String {
        if(picture == "https://unsplash.it/600/300/?random"){
            return ""
        }

        val url = URL(picture)
        val splitUrl = url.path.split("/")
        return splitUrl[2]
    }
}