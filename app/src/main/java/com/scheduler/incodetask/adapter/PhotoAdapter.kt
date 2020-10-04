package com.scheduler.incodetask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scheduler.incodetask.R
import com.scheduler.incodetask.model.PhotoWrapper
import kotlinx.android.synthetic.main.photo_list_item.view.*

class PhotoAdapter(private val photoClickedListener: OnPhotoClickedListener) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    var photoWrapperList = mutableListOf<PhotoWrapper>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false))

    override fun getItemCount() = photoWrapperList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val listItemView = holder.itemView

        val photo = photoWrapperList[position].photo

        listItemView.photoTitleTextView.text = photo.title
        listItemView.photoImageView.layoutParams = listItemView.photoImageView.layoutParams.apply {
            width = photoWrapperList[position].bitmap.width
            height = photoWrapperList[position].bitmap.height
        }
        listItemView.photoImageView.setImageBitmap(photoWrapperList[position].bitmap)

        listItemView.setOnClickListener {
            photoClickedListener.onPhotoClicked(photoWrapperList[position])
        }
    }

    fun addPhoto(photo: PhotoWrapper) {
        photoWrapperList.add(photo)
        notifyDataSetChanged()
    }

    fun addPhoto(index: Int = 0, photo: PhotoWrapper) {
        photoWrapperList.add(index, photo)
        notifyDataSetChanged()
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPhotoClickedListener {
        fun onPhotoClicked(photoWrapper: PhotoWrapper)
    }
}