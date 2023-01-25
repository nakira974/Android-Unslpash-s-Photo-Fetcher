package com.example.tpandroid.photosListComponents

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tpandroid.R
import com.example.tpandroid.data.Urls
import com.squareup.picasso.Picasso
import java.io.ByteArrayInputStream
import java.io.InputStream

class PhotosAdapter(private val onClick: (Urls) -> Unit) :
    ListAdapter<Urls, PhotosAdapter.PhotoViewHolder>(PhotoDiffCallback) {

    /* ViewHolder for Photo, takes in the inflated view and the onClick behavior. */
    class PhotoViewHolder(itemView: View, val onClick: (Urls) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val photoTextView: TextView = itemView.findViewById(R.id.photo_text)
        private val photoImageView: ImageView = itemView.findViewById(R.id.flower_image)
        private var currentPhoto: Urls? = null

        init {
            itemView.setOnClickListener {
                currentPhoto?.let {
                    onClick(it)
                }
            }
        }

        /* Bind photo name and image. */
        fun bind(photo: Urls) {
            currentPhoto = photo

            photoTextView.text = photo.creator_name
            if (photo.small!!.isNotEmpty()) {
                if(photo.is_cached){
                    val inputStream : InputStream = ByteArrayInputStream(photo.image_byteArray);
                    photoImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    photoImageView.adjustViewBounds = true
                    photoImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                }else
                    Picasso.get().load(Uri.parse(photo.small)).into(photoImageView);

            } else {
                Picasso.get()
                    .load(Uri.parse("https://i.insider.com/602ee9ced3ad27001837f2ac?width=700"))
                    .into(photoImageView);
            }
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }
}

object PhotoDiffCallback : DiffUtil.ItemCallback<Urls>() {
    override fun areItemsTheSame(oldItem: Urls, newItem: Urls): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Urls, newItem: Urls): Boolean {
        return oldItem.id == newItem.id
    }
}