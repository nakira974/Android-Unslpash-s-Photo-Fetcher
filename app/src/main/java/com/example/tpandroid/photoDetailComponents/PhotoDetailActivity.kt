package com.example.tpandroid.photoDetailComponents

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tpandroid.PHOTO_ID
import com.example.tpandroid.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.*

class PhotoDetailActivity : AppCompatActivity() {

    private val _viewModelJob = SupervisorJob()
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)

    private val photoDetailViewModel by viewModels<PhotoDetailViewModel> {
        PhotoDetailViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_detail_activity)

        var currentPhotoId: Long? = null

        /* Connect variables to UI elements. */
        val photoCreatorName: TextView = findViewById(R.id.flower_detail_name)
        val photoImageView: ImageView = findViewById(R.id.flower_detail_image)
        val photoDescription: TextView = findViewById(R.id.flower_detail_description)
        val removePhotoButton: Button = findViewById(R.id.remove_button)
        val likePhotoButton: Button = findViewById(R.id.like_button)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentPhotoId = bundle.getLong(PHOTO_ID)
        }

        /* If currentFlowerId is not null, get corresponding flower and set name, image and
        description */
        currentPhotoId?.let {


            val currentPhoto = photoDetailViewModel.getPhotoForId(it)
            when (currentPhoto!!.is_cached) {
                true -> likePhotoButton.text = getString(R.string.unlike_this_photo)
                false -> likePhotoButton.text = getString(R.string.like_this_photo)
            }
            photoCreatorName.text = currentPhoto?.creator_name ?: UUID.randomUUID().toString()
            if (currentPhoto?.small!!.isNotEmpty()) {
                Picasso.get().load(Uri.parse(currentPhoto.small)).into(photoImageView);
            } else {
                Picasso.get()
                    .load(Uri.parse("https://i.insider.com/602ee9ced3ad27001837f2ac?width=700"))
                    .into(photoImageView);
            }
            photoDescription.text = currentPhoto.description

            removePhotoButton.setOnClickListener {
                photoDetailViewModel.removePhoto(currentPhoto)
                finish()
            }

            likePhotoButton.setOnClickListener {
                try {
                    photoDetailViewModel.sendPhotoNotification(currentPhoto)
                    finish()
                } catch (ex: java.lang.Exception) {
                    Log.println(
                        Log.WARN,
                        "DATABASE",
                        "INSERT\\DELETE A LIKED PHOTO INTO THE CACHE TABLE HAS FAILED"
                    )
                }

            }
        }

    }
}