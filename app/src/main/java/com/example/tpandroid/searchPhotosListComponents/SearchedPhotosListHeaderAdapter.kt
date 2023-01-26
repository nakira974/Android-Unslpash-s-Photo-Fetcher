package com.example.tpandroid.searchPhotosListComponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tpandroid.R

/* A list always displaying one element: the number of flowers. */

class SearchedPhotosListHeaderAdapter : RecyclerView.Adapter<SearchedPhotosListHeaderAdapter.HeaderViewHolder>() {
    private var photoCount: Int = 0

    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val photoNumberTextView: TextView = itemView.findViewById(R.id.photo_number_text)

        fun bind(photosCount: Int) {
            //photoNumberTextView.text = photosCount.toString()
        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_header_item, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds number of photos to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(photoCount)
    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }

    /* Updates header to display number of photos when a photo is added or subtracted. */
    fun updateFlowerCount(updatedPhotoCount: Int) {
        photoCount = updatedPhotoCount
        notifyDataSetChanged()
    }
}