package com.scheduler.incodetask.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.scheduler.incodetask.R
import com.scheduler.incodetask.activity.MainActivity.Companion.PHOTO_KEY
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.service.PhotoConverterService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val photo = intent.getSerializableExtra(PHOTO_KEY) as Photo

        titleTextView.text = photo.title
        descriptionTextView.text = photo.comment

        CompositeDisposable().add(PhotoConverterService().getBitmapFromUrl(photo)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                photoImageView.setImageBitmap(it.second)
            }, {
                throw it
            }))

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
}