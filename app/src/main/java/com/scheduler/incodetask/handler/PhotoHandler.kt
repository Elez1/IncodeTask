package com.scheduler.incodetask.handler

import android.graphics.Bitmap
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.model.PhotoWrapper
import com.scheduler.incodetask.repository.PhotoRepository
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

class PhotoHandler @Inject constructor(repository: PhotoRepository) {

    @Inject
    lateinit var repository: PhotoRepository

    var photoWrapperList = mutableListOf<PhotoWrapper>()

    suspend fun getPhotos(): MutableList<PhotoWrapper> {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val photos = repository.getPhotos()
            fillPhotoWrapperList(photos).joinAll()
            sortList(photos, photoWrapperList)
        }
        job.join()
        return photoWrapperList
    }

    private fun sortList(photos: MutableList<Photo>, photoWrapperList: MutableList<PhotoWrapper>) {
        val sortedList = mutableListOf<PhotoWrapper>()

        for (photo in photos) {
            val wrapper = getPhotoWrapperForPhoto(photo, photoWrapperList)
            sortedList.add(wrapper)
        }

        this.photoWrapperList = sortedList
    }

    private fun getPhotoWrapperForPhoto(photo: Photo, list: MutableList<PhotoWrapper>): PhotoWrapper {
        for (pw in list) {
            if (pw.photo == photo) {
                return pw
            }
        }
        throw Exception("Wrapper not found!")
    }

    private suspend fun fillPhotoWrapperList(mutableList: MutableList<Photo>) = withContext(Dispatchers.IO) {
        val jobList = ConcurrentLinkedQueue<Job>()
        for (photo in mutableList) {
            jobList.add(GlobalScope.launch {
                val photoWrapper = createPhotoWrapper(photo)
                photoWrapper.photo.replacePictureUrlWithId(photoWrapper.id)
                photoWrapperList.add(photoWrapper)
//                uncomment this to add one by one
//                photoListener.onPhotosReady(photoWrapper)
            })
        }

        jobList
    }

    fun getPhotoFromUrl(stringUrl:String): Deferred<Bitmap> {
        return repository.getBitmapFromUrlAsync(stringUrl)
    }

    private suspend fun createPhotoWrapper(photo: Photo): PhotoWrapper {
        val photoId = repository.getPhotoIdAndBitmapPair(photo.picture).await()
        return PhotoWrapper(photoId.first, photo, photoId.second)
    }

    interface PhotoListener {
        fun onPhotosReady(photoWrapper: PhotoWrapper)
    }

}