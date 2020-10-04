package com.scheduler.incodetask.model

import android.graphics.Bitmap

data class PhotoWrapper(var id:String, var photo: Photo, var bitmap: Bitmap){

    companion object{
        fun createUserPhoto(bitmap: Bitmap, path: String) = PhotoWrapper("userPhoto", Photo("userPhoto", "Title", "This is User photo", "${System.currentTimeMillis()}", path), bitmap)
    }

    fun isUserPhoto() = id == "userPhoto"
}