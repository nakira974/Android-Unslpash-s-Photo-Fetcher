package com.example.tpandroid.photoDetailComponents

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tpandroid.App
import com.example.tpandroid.BuildConfig
import com.example.tpandroid.data.DataSource
import com.example.tpandroid.data.Photo
import com.example.tpandroid.data.Urls
import com.example.tpandroid.services.UnsplashPhotoService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class PhotoDetailViewModel(private val datasource: DataSource) : ViewModel() {

    private val _viewModelJob = SupervisorJob()
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)

    /* Queries datasource to returns a photo that corresponds to an id. */
    fun getPhotoForId(id: Long): Urls? {
        var result: Urls? = null
        try {
            runBlocking {
                val selectEntityTask = _uiScope.launch(Dispatchers.IO) {
                    result =
                        App.database.photosRepository().loadAllByIds(intArrayOf(id.toInt())).get(0)
                    Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id HAS BEEN SELECTED")
                }
                selectEntityTask.join()
            }
        } catch (e: java.lang.Exception) {
            Log.println(Log.WARN, "DATABASE", e.message.toString())
        }

        return result
    }

    /* Queries datasource to remove a photo. */
    fun removePhoto(photo: Urls) {
        try {
            runBlocking {
                val selectEntityTask = _uiScope.launch(Dispatchers.IO) {
                    val id = photo.id;
                    App.database.photosRepository().deleteById(id.toInt())
                    Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id HAS BEEN ERASED")
                }
                selectEntityTask.join()
            }
        } catch (e: java.lang.Exception) {
            Log.println(Log.WARN, "DATABASE", e.message.toString())
        }
    }

    private fun isPhotoCached(photo: Urls): Boolean {
        var result = false
        try {
            runBlocking {
                val selectEntityTask = _uiScope.launch(Dispatchers.IO) {
                    val id = photo.id;
                    result = App.database.photosRepository().findByIdIfCached(id.toInt())
                    when (result) {
                        true -> Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id IS CACHED")
                        false -> Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id IS NOT CACHED")
                    }
                }
                selectEntityTask.join()
            }
        } catch (e: java.lang.Exception) {
            Log.println(Log.WARN, "DATABASE", e.message.toString())
        }

        return result
    }

    fun cachePhoto(photo: Urls) {
        try {
            runBlocking {
                val selectEntityTask = _uiScope.launch(Dispatchers.IO) {
                    val id = photo.id;
                    val bos = ByteArrayOutputStream()
                    Picasso.get().load(photo.small).get().compress(Bitmap.CompressFormat.PNG, 100, bos)
                    val bArray = bos.toByteArray()
                    photo.image_byteArray = bArray;
                    App.database.photosRepository().setCachedById(photo.id.toInt(), photo.is_cached, photo.image_byteArray)
                    when (photo.is_cached) {
                        true -> Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id IS NOW PERSISTENT")
                        false -> Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id IS NOW NO LONGER PERSISTENT")
                    }
                }
                selectEntityTask.join()
            }
        } catch (e: java.lang.Exception) {
            Log.println(Log.WARN, "DATABASE", e.message.toString())
        }

    }

    fun sendPhotoNotification(photo: Urls) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UnsplashPhotoService::class.java)
        val sendNotificationTask: Call<Photo>
        val imageId = photo.image_id
        sendNotificationTask = when (isPhotoCached(photo)) {
            true -> {
                photo.is_cached = false
                service.deleteLikePhoto(token = BuildConfig.UNSPLASH_API_KEY, imageId)
            }
            false -> {
                photo.is_cached = true
                service.postLikePhoto(token = BuildConfig.UNSPLASH_API_KEY, imageId)
            }
        }

        sendNotificationTask.enqueue(object : Callback<Photo> {

            override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                cachePhoto(photo)
                when (response.code()) {
                    201 -> {
                        Log.println(
                            Log.DEBUG,
                            "HTTP",
                            "PHOTO ID:$imageId HAS BEEN LIKED FROM https://api.unsplash.com/photos/$imageId/like"
                        )
                    }
                    200 -> {
                        Log.println(
                            Log.DEBUG,
                            "HTTP",
                            "PHOTO ID:$imageId HAS BEEN UNLIKED FROM https://api.unsplash.com/photos/$imageId/unlike"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<Photo>, t: Throwable) {
                Log.println(Log.WARN, "HTTP", "SEND LIKE\\UNLIKE NOTIFICATION REQUEST HAS FAILED")
            }
        })
    }
}

class PhotoDetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoDetailViewModel(
                datasource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}