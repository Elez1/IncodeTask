package com.scheduler.incodetask.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.content.FileProvider
import com.scheduler.incodetask.BuildConfig
import com.scheduler.incodetask.R
import com.scheduler.incodetask.application.IncodeApplication
import com.scheduler.incodetask.extensions.toBytes
import com.scheduler.incodetask.handler.PhotoHandler
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.service.BitmapService
import com.scheduler.incodetask.service.FileService
import com.scheduler.incodetask.view.activity.MainActivity.Companion.PHOTO_KEY
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject


class PhotoActivity : BaseActivity() {

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)

    @Inject
    lateinit var fileService: FileService

    @Inject
    lateinit var bitmapService: BitmapService

    @Inject
    lateinit var photoHandler: PhotoHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        (application as IncodeApplication).appComponent.injectPhotoActivity(this)

        val photo = intent.getSerializableExtra(PHOTO_KEY) as Photo

        titleTextView.text = photo.title
        descriptionTextView.text = photo.comment

        showSpinner()
        var bitmap: Bitmap? = null
        coroutineScope.launch {
            bitmap = if (photo._id == "userPhoto") {
                val b = BitmapFactory.decodeFile(photo.picture)
                bitmapService.resize(b, 1000, 1000) ?: throw Exception("Compressed photo can't be null")
            } else {
                photoHandler.getPhotoFromUrl(photo.picture).await()
            }

            setBitmap(bitmap!!)
            hideSpinner()
        }

        shareButton.setOnClickListener {
            val uri = getFileUri(photo, bitmap!!)

            val intent = Intent(Intent.ACTION_SEND)

            intent.putExtra(Intent.EXTRA_STREAM, uri)

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/png"
            startActivity(intent)
            fileService.deleteFile(File(uri.path!!))
        }
    }

    private fun getFileUri(photo: Photo, bitmap: Bitmap) = if (photo._id == "userPhoto") {
        FileProvider.getUriForFile(this@PhotoActivity, BuildConfig.APPLICATION_ID + ".provider", File(photo.picture))
    } else {
        val pictureFile = fileService.getOutputMediaFile()
        fileService.saveBitmap(bitmap.toBytes(), pictureFile!!)
        FileProvider.getUriForFile(this@PhotoActivity, BuildConfig.APPLICATION_ID + ".provider", pictureFile)
    }

    private suspend fun setBitmap(bitmap: Bitmap) = withContext(Dispatchers.Main) {
        photoImageView.setImageBitmap(bitmap)
    }

    override fun onStop() {
        if (coroutineJob.isActive) {
            coroutineJob.cancel()
        }

        super.onStop()
    }
}