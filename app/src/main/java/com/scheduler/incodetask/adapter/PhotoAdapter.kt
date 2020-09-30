package com.scheduler.incodetask.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.model.Photo
import com.scheduler.incodetask.service.PhotoConverterService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.photo_list_item.view.*

class PhotoAdapter(private val photoClickedListener: OnPhotoClickedListener) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    var listOfPhotos = mutableListOf<Photo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false))

    override fun getItemCount() = listOfPhotos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val listItemView = holder.itemView
        val photo = listOfPhotos[position]

//        listItemView.photoTitleTextView.text = photo.title

        // TODO: 9/29/20 Fix - move to activity and place only bitmaps here or think of something new
        CompositeDisposable().add(PhotoConverterService().getBitmapFromUrl(photo.picture)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                Log.e("sdfsdsdf", "Setting photo on position $position: ${photo.picture}")
                listItemView.photoImageView.setImageBitmap(it)
            }, {
                throw it
            })
        )

        listItemView.setOnClickListener {
            photoClickedListener.onPhotoClicked(photo)
        }
    }

    fun addPhoto(bitmap: Bitmap){

    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPhotoClickedListener{
        fun onPhotoClicked(photo: Photo)
    }
}