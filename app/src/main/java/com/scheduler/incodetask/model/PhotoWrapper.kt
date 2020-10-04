package com.scheduler.incodetask.model

import android.graphics.Bitmap

data class PhotoWrapper(var id:String, var photo: Photo, var bitmap: Bitmap){

    companion object{
        val USER_PHOTO_ID = "userPhoto"
        fun createUserPhoto(bitmap: Bitmap, path: String) = PhotoWrapper(USER_PHOTO_ID, Photo(USER_PHOTO_ID, "Title", "This is User photo", "${System.currentTimeMillis()}", path), bitmap)
    }
}