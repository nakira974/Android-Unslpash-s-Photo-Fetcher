package com.example.tpandroid.searchPhotosListComponents

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchedPhotosListFragment: Fragment() {
    private var _binding: FragmentPhotosListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val displayLikedPhotosListActivityCode = 1
    private val _viewModelJob = SupervisorJob()
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)

    private var _userInput : String = ""

    private val photosListViewModel by viewModels<PhotosListViewModel> {
        PhotosListViewModelFactory(this.requireContext())
    }


    override fun onResume() {
        super.onResume()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView: View = inflater.inflate(R.layout.fragment_searched_photos_list, container, false)

        _binding = FragmentPhotosListBinding.inflate(inflater, container, false)

        try{
            val textInputLayout: TextInputLayout = rootView.findViewById(R.id.search_photo_text_layout)
            val textInputEditText: TextInputEditText = textInputLayout.findViewById(R.id.search_photos_text_input)
            textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    var photosLiveData: LiveData<List<Urls>>? = null
                    var photos: List<Photo>? = emptyList()
                    _userInput = s.toString()
                    val semaphore = Semaphore(1)
                    /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
       which displays the contents sequentially */
                    val searchedPhotosListHeaderAdapter = SearchedPhotosListHeaderAdapter()
                    val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
                    val concatAdapter = ConcatAdapter(searchedPhotosListHeaderAdapter, photosAdapter)
                    val recyclerView: RecyclerView =rootView.findViewById(R.id.searched_photos_recycler_view)
                    recyclerView.adapter = concatAdapter
                    recyclerView.layoutManager = LinearLayoutManager(activity);

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
                        Log.println(Log.DEBUG, "DATABASE", "OLD SEARCHED PHOTOS URLS HAS BEEN CLEARED")

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
                                "PHOTOS URLS HAS BEEN FILLED FROM SEARCHED PHOTOS"
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
                                searchedPhotosListHeaderAdapter.updateFlowerCount(it.size)
                            }
                        }

                        Log.println(Log.DEBUG, "OBSERVER", "SET")

                    } catch (e: java.lang.Exception) {
                        Log.println(Log.ERROR, "DATABASE", e.message.toString())
                    }
                }

            })

        }catch (e : Exception){
            Log.println(Log.WARN, "SEARCHED_PHOTOS", "ERROR")
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