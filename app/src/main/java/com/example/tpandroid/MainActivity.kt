package com.example.tpandroid

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tpandroid.*
import com.example.tpandroid.data.DataSource
import com.example.tpandroid.data.Photo
import com.example.tpandroid.data.Urls
import com.example.tpandroid.databinding.ActivityMainBinding
import com.example.tpandroid.services.UnsplashPhotoService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

const val PHOTO_ID = "flower id"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val _viewModelJob = SupervisorJob()
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)


    /* Queries datasource to update photo's likes. */
    private fun updatePhotoLikes(photo: Urls) {
        try {
            runBlocking {
                val selectEntityTask = _uiScope.launch(Dispatchers.IO) {
                    val id = photo.id;
                    App.database.photosRepository().updateLikesById(id.toInt(), photo.like_number)
                    Log.println(Log.DEBUG, "DATABASE", "PHOTO ID:$id LIKES HAS BEEN UPDATED")
                }
                selectEntityTask.join()
            }
        } catch (e: java.lang.Exception) {
            Log.println(Log.WARN, "DATABASE", e.message.toString())
        }
    }
    private fun getPhotoLikes(photo : Urls){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UnsplashPhotoService::class.java)
        val imageId = photo.image_id
        val getPhotoStatisticsTask : Call<Photo> = service.getPhotoLikes(BuildConfig.UNSPLASH_API_KEY, photo.image_id)

        getPhotoStatisticsTask.enqueue(object : Callback<Photo> {

            override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                when (response.code()) {
                    200 -> {
                        photo.like_number = response.body()!!.likes
                        updatePhotoLikes(photo)
                        Log.println(
                            Log.DEBUG,
                            "HTTP",
                            "PHOTO ID:$imageId LIKES HAS BEEN UPDATED FROM https://api.unsplash.com/photos/$imageId/statistics"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<Photo>, t: Throwable) {
                Log.println(Log.WARN, "HTTP", "GET PHOTO ID:$imageId LIKES REQUEST HAS FAILED")
            }
        })
    }



    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            val datasource : DataSource = DataSource(resources)
            val likedPhotos = datasource.getLikedPhotosList().getOrNull()!!.value
            likedPhotos!!.forEach(){
                    getPhotoLikes(it)
            }
        }catch (ex : Exception){
            Log.println(Log.ERROR, "DATABASE", "LIKED PHOTOS CANNOT BE UPDATED")
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_photos_list, R.id.navigation_liked_photos, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




}