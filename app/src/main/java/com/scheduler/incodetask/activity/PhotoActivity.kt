package com.scheduler.incodetask.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import com.scheduler.incodetask.R
import com.scheduler.incodetask.activity.MainActivity.Companion.PHOTO_KEY
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.repository.PhotoRepository
import com.scheduler.incodetask.service.BitmapService
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.coroutines.*


class PhotoActivity : BaseActivity() {

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val photo = intent.getSerializableExtra(PHOTO_KEY) as Photo

        titleTextView.text = photo.title
        descriptionTextView.text = photo.comment

        showSpinner()
        coroutineScope.launch {
            val bitmap = if (photo._id == "userPhoto") {
                val bitmap = BitmapFactory.decodeFile(photo.picture)
                BitmapService().resize(bitmap, 1000, 1000) ?: throw Exception("Compressed photo can't be null")
            } else {
                PhotoRepository().getBitmapFromUrlAsync(photo.picture).await()
            }
            setBitmap(bitmap)
            hideSpinner()
        }

        shareButton.setOnClickListener {
//            val path: String = MediaStore.Images.Media.insertImage(contentResolver, photo, "Design", null)
//
//            val uri: Uri = Uri.parse(path)
//
//            val share = Intent(Intent.ACTION_SEND)
//            share.type = "image"
//            share.putExtra(Intent.EXTRA_STREAM, uri)
//            share.setType("image/*")
//            share.putExtra(Intent.EXTRA_TEXT, "I found something cool!")
//            startActivity(Intent.createChooser(share, "Share Your Design!"))
        }
    }


    private suspend fun setBitmap(bitmap: Bitmap) = withContext(Dispatchers.Main) {
        photoImageView.setImageBitmap(bitmap)
    }

    override fun onPause() {
        if (coroutineJob.isActive) {
            coroutineJob.cancel()
        }

        super.onPause()
    }
}