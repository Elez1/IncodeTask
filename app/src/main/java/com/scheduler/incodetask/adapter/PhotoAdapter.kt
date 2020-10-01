package com.scheduler.incodetask.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scheduler.incodetask.R
import com.scheduler.incodetask.model.PhotoWrapper
import kotlinx.android.synthetic.main.photo_list_item.view.*

class PhotoAdapter(private val photoClickedListener: OnPhotoClickedListener) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

//    var bitmapList = mutableListOf<Pair<String, Bitmap>>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//    var listOfPhotos = mutableListOf<Photo>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    var photoWrapperList = mutableListOf<PhotoWrapper>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false))

    override fun getItemCount() = photoWrapperList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val listItemView = holder.itemView
//        val bitmapPair = bitmapList[position]
//        val photo = listOfPhotos[position]

        val photo = photoWrapperList[position].photo
        val bitmap = photoWrapperList[position].bitmap

//        Glide.with(listItemView.context).load(Uri.parse(photo.picture)).into(listItemView.photoImageView)

        listItemView.photoTitleTextView.text = photo.title
        listItemView.photoImageView.setImageBitmap(bitmap)

        listItemView.setOnClickListener {
            photoClickedListener.onPhotoClicked(photoWrapperList[position])
        }
    }

    fun addPhoto(photo: PhotoWrapper) {
        photoWrapperList.add(0, photo)
        notifyDataSetChanged()
    }

//    fun addPhoto(index: Int, pair: Pair<String, Bitmap>) {
//        if (photoAlreadyExists(pair)) {
//            return
//        }
//        bitmapList.add(index, pair)
//        notifyDataSetChanged()
//    }
//
//    fun addPhoto(pair: Pair<String, Bitmap>) {
//        if (photoAlreadyExists(pair)) {
//            return
//        }
//        bitmapList.add(pair)
//        notifyDataSetChanged()
//    }
//
//    private fun photoAlreadyExists(pair: Pair<String, Bitmap>): Boolean {
//        for (p in bitmapList) {
//            if (p.first == pair.first && p.second == pair.second) {
//                return true
//            }
//        }
//        return false
//    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPhotoClickedListener {
        fun onPhotoClicked(photo: PhotoWrapper)
    }
}