package com.example.tpandroid.photosListComponents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tpandroid.*
import com.example.tpandroid.addPhoto.AUTHOR_NAME
import com.example.tpandroid.addPhoto.PHOTO_DESCRIPTION
import com.example.tpandroid.data.Photo
import com.example.tpandroid.data.Urls
import com.example.tpandroid.databinding.ActivityMainBinding
import com.example.tpandroid.likedPhotosListComponents.LikedPhotosListActivity
import com.example.tpandroid.photoDetailComponents.PhotoDetailActivity
import com.example.tpandroid.services.UnsplashPhotoService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

const val FLOWER_ID = "flower id"

class PhotosListActivity : AppCompatActivity() {
    private val displayLikedPhotosListActivityCode = 1
    private val _viewModelJob = SupervisorJob()
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)
    private lateinit var binding: ActivityMainBinding

    private val photosListViewModel by viewModels<PhotosListViewModel> {
        PhotosListViewModelFactory(this)
    }

    override fun onRestart() {
        super.onRestart()
        val owner = this
        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = concatAdapter
        val photosLiveData: LiveData<List<Urls>>? = photosListViewModel.photosLiveData.getOrNull()

        photosLiveData?.observe(owner) {
            it?.let {
                photosAdapter.submitList(it as MutableList<Urls>)
                headerAdapter.updateFlowerCount(it.size)
            }
        }

        Log.println(Log.DEBUG, "OBSERVER", "UPDATED")

    }

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_navigation_bottom)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                )
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

        }catch (e : Exception){
            Log.println(Log.ERROR, "NAV BAR", "INFLATE ERROR")
            Log.println(Log.ERROR, "NAV BAR", e.message.toString())
            Log.println(Log.ERROR, "NAV BAR", e.stackTrace.toString())

        }


        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        var photosLiveData: LiveData<List<Urls>>? = null
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val owner = this
        recyclerView.adapter = concatAdapter
        var photos: List<Photo>? = emptyList()
        val semaphore = Semaphore(1)


        val cleanDatabaseTask = _uiScope.launch(Dispatchers.Default) {
            semaphore.acquire()
            Room.databaseBuilder(
                applicationContext,
                ApplicationDbContext::class.java,
                "recyclersample.dat"
            )
                .fallbackToDestructiveMigration().build()

            App.database.photosRepository().getAllUnCached().forEach() {
                App.database.photosRepository().delete(it)
            }
            Log.println(Log.DEBUG, "DATABASE", "OLD UNCACHED PHOTOS URLS HAS BEEN CLEARED")

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.unsplash.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(UnsplashPhotoService::class.java)

            val getRandomPhotosTask =
                service.getRandomPhotos(token = BuildConfig.UNSPLASH_API_KEY)


            photos = getRandomPhotosTask.execute().body()
            Log.println(
                Log.DEBUG,
                "HTTP",
                "PHOTOS HAS BEEN FETCHED FROM https://api.unsplash.com/photos/random?count=11&topics=nature"
            )

            semaphore.release()
        }
        val fillDataBaseTask = _uiScope.launch(Dispatchers.Default) {
            semaphore.acquire()
            if (photos!!.isNotEmpty()) {
                //On insère les URLS de la photo en base
                photos!!.forEach() {
                    it.urls!!.description = it.description.toString()
                    it.urls!!.creator_name = it.user!!.name.toString()
                    it.urls!!.download_url = it.links!!.download.toString()
                    it.urls!!.image_id = it.id!!.toString()

                    App.database.photosRepository().insertAll(it.urls!!)
                }
                Log.println(
                    Log.DEBUG,
                    "DATABASE",
                    "PHOTOS URLS HAS BEEN FILLED FROM THE DAILY UPDATE"
                )
                photosLiveData = photosListViewModel.photosLiveData.getOrNull()


            }
            semaphore.release()
        }


        try {
            runBlocking {
                cleanDatabaseTask.join()
                fillDataBaseTask.join()
            }
            photosLiveData?.observe(owner) {
                it?.let {
                    photosAdapter.submitList(it as MutableList<Urls>)
                    headerAdapter.updateFlowerCount(it.size)
                }
            }

            Log.println(Log.DEBUG, "OBSERVER", "SET")

            val fab: View = findViewById(R.id.fab)
            fab.setOnClickListener {
                fabOnClick()
            }

        } catch (e: java.lang.Exception) {
            Log.println(Log.ERROR, "DATABASE", e.message.toString())
        }


    }

    /* Opens FlowerDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(photo: Urls) {
        val intent = Intent(this, PhotoDetailActivity()::class.java)
        intent.putExtra(FLOWER_ID, photo.id)
        startActivity(intent)
    }

    /* Adds flower to flowerList when FAB is clicked. */
    private fun fabOnClick() {
        val intent = Intent(this, LikedPhotosListActivity::class.java)
        startActivityForResult(intent, displayLikedPhotosListActivityCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        /* Inserts flower into viewModel. */
        if (requestCode == displayLikedPhotosListActivityCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val photoAuthorName = data.getStringExtra(AUTHOR_NAME)
                val photoDescription = data.getStringExtra(PHOTO_DESCRIPTION)

                //TODO INSERT WITH DB
                //photosListViewModel.insertFlower(flowerName, flowerDescription)
            }
        }
    }
}