package com.example.tpandroid.likedPhotosListComponents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tpandroid.R
import com.example.tpandroid.data.Urls
import com.example.tpandroid.photoDetailComponents.PhotoDetailActivity
import com.example.tpandroid.photosListComponents.*
import com.example.tpandroid.photosListComponents.PhotosListViewModelFactory

class LikedPhotosListActivity : AppCompatActivity() {

    private val backToMainActivity = 1
    private val likedPhotosListViewModel by viewModels<LikedPhotosListViewModel> {
        LikedPhotosListViewModelFactory(this)
    }

    override fun onRestart() {
        super.onRestart()
        val owner = this
        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = concatAdapter
        val photosLiveData: LiveData<List<Urls>>? = likedPhotosListViewModel.likedPhotosLiveData.getOrNull()

        photosLiveData?.observe(owner) {
            it?.let {
                photosAdapter.submitList(it as MutableList<Urls>)
                headerAdapter.updateFlowerCount(it.size)
            }
        }

        Log.println(Log.DEBUG, "OBSERVER", "UPDATED")

    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val owner = this
        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = concatAdapter
        val photosLiveData: LiveData<List<Urls>>? = likedPhotosListViewModel.likedPhotosLiveData.getOrNull()

        photosLiveData?.observe(owner) {
            it?.let {
                photosAdapter.submitList(it as MutableList<Urls>)
                headerAdapter.updateFlowerCount(it.size)
            }
        }

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }

    }

    private fun adapterOnClick(photo: Urls) {
        val intent = Intent(this, PhotoDetailActivity()::class.java)
        intent.putExtra(FLOWER_ID, photo.id)
        startActivity(intent)
    }
    private fun fabOnClick() {
        val intent = Intent(this, PhotosListActivity::class.java)
        startActivityForResult(intent, backToMainActivity)
    }

}