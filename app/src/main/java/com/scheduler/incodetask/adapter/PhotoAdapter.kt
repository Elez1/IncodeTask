package com.scheduler.incodetask.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.model.Photo
import kotlinx.android.synthetic.main.photo_list_item.view.*

class PhotoAdapter(private val photoClickedListener: OnPhotoClickedListener) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    var bitmapList = mutableListOf<Pair<String, Bitmap>>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listOfPhotos = mutableListOf<Photo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false))

    override fun getItemCount() = bitmapList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val listItemView = holder.itemView
        val bitmapPair = bitmapList[position]
        val photo = listOfPhotos[position]

        listItemView.photoTitleTextView.text = photo.title
        listItemView.photoImageView.setImageBitmap(bitmapPair.second)

        listItemView.setOnClickListener {
            photoClickedListener.onPhotoClicked(listOfPhotos[position])
        }
    }

    fun addPhoto(index: Int, pair: Pair<String, Bitmap>) {
        if (photoAlreadyExists(pair)) {
            return
        }
        bitmapList.add(index, pair)
        notifyDataSetChanged()
    }

    fun addPhoto(pair: Pair<String, Bitmap>) {
        if (photoAlreadyExists(pair)) {
            return
        }
        bitmapList.add(pair)
        notifyDataSetChanged()
    }

    private fun photoAlreadyExists(pair: Pair<String, Bitmap>): Boolean {
        for (p in bitmapList) {
            if (p.first == pair.first && p.second == pair.second) {
                return true
            }
        }
        return false
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPhotoClickedListener {
        fun onPhotoClicked(photo: Photo)
    }
}