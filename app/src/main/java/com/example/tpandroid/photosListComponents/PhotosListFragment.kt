package com.example.tpandroid.photosListComponents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tpandroid.*
import com.example.tpandroid.data.Photo
import com.example.tpandroid.data.Urls
import com.example.tpandroid.databinding.FragmentPhotosListBinding
import com.example.tpandroid.photoDetailComponents.PhotoDetailActivity
import com.example.tpandroid.services.UnsplashPhotoService
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhotosListFragment: Fragment() {
    private var _binding: FragmentPhotosListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val displayLikedPhotosListActivityCode = 1
    private val _viewModelJob = SupervisorJob()
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)

    private val photosListViewModel by viewModels<PhotosListViewModel> {
        PhotosListViewModelFactory(this.requireContext())
    }


    override fun onResume() {
        super.onResume()
        val owner = this
        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        val recyclerView: RecyclerView = requireView().findViewById(R.id.photos_recycler_view)
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView: View = inflater.inflate(R.layout.fragment_photos_list, container, false)

        _binding = FragmentPhotosListBinding.inflate(inflater, container, false)


        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        var photosLiveData: LiveData<List<Urls>>? = null
        val recyclerView: RecyclerView =rootView.findViewById(R.id.photos_recycler_view)
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity);

        var photos: List<Photo>? = emptyList()
        val semaphore = Semaphore(1)

        val cleanDatabaseTask = _uiScope.launch(Dispatchers.Default) {
            semaphore.acquire()
            Room.databaseBuilder(
                requireActivity().applicationContext,
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
                service.getRandomPhotos(token = com.example.tpandroid.BuildConfig.UNSPLASH_API_KEY)


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
                    it.urls!!.image_byteArray = ByteArray(0)

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
            photosLiveData?.observe(viewLifecycleOwner) {
                it?.let {
                    photosAdapter.submitList(it as MutableList<Urls>)
                    headerAdapter.updateFlowerCount(it.size)
                }
            }

            Log.println(Log.DEBUG, "OBSERVER", "SET")

        } catch (e: java.lang.Exception) {
            Log.println(Log.ERROR, "DATABASE", e.message.toString())
        }

        return rootView
    }


    private fun adapterOnClick(photo: Urls) {
        val intent = Intent(requireActivity(), PhotoDetailActivity()::class.java)
        intent.putExtra(PHOTO_ID, photo.id)
        startActivity(intent)
    }

    /* Adds flower to flowerList when FAB is clicked. */
    private fun fabOnClick() {
        findNavController().navigate(R.id.navigation_liked_photos)
    }

}