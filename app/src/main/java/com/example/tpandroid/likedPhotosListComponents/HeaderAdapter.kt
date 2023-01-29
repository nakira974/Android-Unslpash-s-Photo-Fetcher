package com.example.tpandroid.likedPhotosListComponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tpandroid.R

/* A list always displaying one element: the number of flowers. */

class HeaderAdapter : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {
    private var likedPhotoCount: Int = 0

    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val photoNumberTextView: TextView = itemView.findViewById(R.id.photo_number_text)
        private var likedPhotoHeaderTextView  : TextView = itemView.findViewById(R.id.header_text)
        private var likedPhotoTextView  : TextView = itemView.findViewById(R.id.header_photo_text)

        fun bind(likedPhotosCount: Int) {
            photoNumberTextView.text = likedPhotosCount.toString()
            likedPhotoHeaderTextView.text = "Favorites finder"
            likedPhotoTextView.text = "Liked photos"
        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_item, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds number of photos to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(likedPhotoCount)
    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }

    /* Updates header to display number of photos when a photo is added or subtracted. */
    fun updateFlowerCount(updatedPhotoCount: Int) {
        likedPhotoCount = updatedPhotoCount
        notifyDataSetChanged()
    }
}