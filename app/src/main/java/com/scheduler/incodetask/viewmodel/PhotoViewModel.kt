package com.scheduler.incodetask.viewmodel

import android.util.Log
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.repository.PhotoRepository
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.ConcurrentLinkedQueue

class PhotoViewModel(private val photoListener: PhotoListener) {

    private val repository = PhotoRepository()

    var photoWrapperList = mutableListOf<PhotoWrapper>()

    suspend fun getPhotos(): MutableList<PhotoWrapper> {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val photos = repository.getPhotos()
            fillPhotoWrapperList(photos)
            sortList(photos, photoWrapperList)

//            for (photo in photos) {
//                val photoWrapper = getBitmapAndId(photo)
//                withContext(Dispatchers.Main) {
//                    Log.e("sdfsdfsdf", "Adding photo to the list")
//                    photoWrapperList.add(photoWrapper!!)
//                    photoListener.onPhotosReady(photoWrapper)
//                }
//            }

        }
        job.join()
        return photoWrapperList
    }

    private fun sortList(photos: MutableList<Photo>, photoWrapperList: MutableList<PhotoWrapper>) {
        val sortedList = mutableListOf<PhotoWrapper>()

        for (photo in photos){
            val wrapper = getPhotoWrapperForPhoto(photo, photoWrapperList)
            sortedList.add(wrapper)
        }

        for(pw in sortedList){
            Log.e("sdfsdfsdf", pw.toString())
        }
        this.photoWrapperList = sortedList
    }

    private fun getPhotoWrapperForPhoto(photo: Photo, list: MutableList<PhotoWrapper>): PhotoWrapper {
        for (pw in list){
            if (pw.photo == photo){
                return pw
            }
        }
        throw Exception("Wrapper not found!")
    }

    private suspend fun getBitmapAndId(photo: Photo): PhotoWrapper {
        return createPhotoWrapper(photo)
    }

    private suspend fun fillPhotoWrapperList(mutableList: MutableList<Photo>) = withContext(Dispatchers.IO) {
        val jobList = ConcurrentLinkedQueue<Job>()
        for (photo in mutableList) {
            jobList.add(GlobalScope.launch {
                val photoWrapper = createPhotoWrapper(photo)
                photoWrapper.photo.replacePictureUrlWithId(photoWrapper.id)
                photoWrapperList.add(photoWrapper)
//                photoListener.onPhotosReady(pw)
            })
        }

        jobList.joinAll()
    }

    private suspend fun createPhotoWrapper(photo: Photo): PhotoWrapper {
        val photoId = repository.getPhotoIdAndBitmapPair(photo.picture).await()
        return PhotoWrapper(photoId.first, photo, photoId.second)
    }

    interface PhotoListener {
        fun onPhotosReady(photoWrapper: PhotoWrapper)
    }

}