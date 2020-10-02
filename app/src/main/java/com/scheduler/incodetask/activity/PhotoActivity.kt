package com.scheduler.incodetask.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scheduler.incodetask.R
import com.scheduler.incodetask.activity.MainActivity.Companion.PHOTO_KEY
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.repository.PhotoRepository
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.coroutines.*


class PhotoActivity : AppCompatActivity() {

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val photo = intent.getSerializableExtra(PHOTO_KEY) as Photo

        titleTextView.text = photo.title
        descriptionTextView.text = photo.comment

        if (photo._id == "userPhoto") {
            val bitmap = BitmapFactory.decodeFile(photo.picture)

            val width = bitmap.width
            val height = bitmap.height
            val aspectRatio = 0.5
            val newHeight = 300
            val newWidth = (newHeight * aspectRatio).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)

            photoImageView.setImageBitmap(scaledBitmap)
        } else {
            coroutineScope.launch {
                val bitmap = PhotoRepository().getBitmapFromUrlAsync(photo.picture).await()

                withContext(Dispatchers.Main) {
                    photoImageView.setImageBitmap(bitmap)
                }
            }
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

    override fun onPause() {
        if (coroutineJob.isActive) {
            coroutineJob.cancel()
        }

        super.onPause()
    }
}