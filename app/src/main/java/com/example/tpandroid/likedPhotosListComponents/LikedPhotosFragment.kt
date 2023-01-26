package com.example.tpandroid.likedPhotosListComponents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tpandroid.MainActivity
import com.example.tpandroid.PHOTO_ID
import com.example.tpandroid.R
import com.example.tpandroid.data.Urls
import com.example.tpandroid.databinding.FragmentPhotosListBinding
import com.example.tpandroid.photoDetailComponents.PhotoDetailActivity
import com.example.tpandroid.PhotosAdapter

class LikedPhotosFragment : Fragment() {

    private var _binding: FragmentPhotosListBinding? = null
    private val binding get() = _binding!!

    private val backToMainActivity = 1
    private val likedPhotosListViewModel by viewModels<LikedPhotosListViewModel> {
        LikedPhotosListViewModelFactory(requireContext())
    }

    override fun onResume() {
        super.onResume()
        val owner = this

        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        val recyclerView: RecyclerView = requireView().rootView.findViewById(R.id.liked_photos_recycler_view)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root: View = inflater.inflate(R.layout.fragment_liked_photos_list, container, false)


        val headerAdapter = HeaderAdapter()
        val photosAdapter = PhotosAdapter { photo -> adapterOnClick(photo) }
        val concatAdapter = ConcatAdapter(headerAdapter, photosAdapter)
        val recyclerView: RecyclerView = root.findViewById(R.id.liked_photos_recycler_view)
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity);
        val photosLiveData: LiveData<List<Urls>>? = likedPhotosListViewModel.likedPhotosLiveData.getOrNull()

        photosLiveData?.observe(requireActivity()) {
            it?.let {
                photosAdapter.submitList(it as MutableList<Urls>)
                headerAdapter.updateFlowerCount(it.size)
            }
        }

        return root
    }

    private fun adapterOnClick(photo: Urls) {
        val intent = Intent(requireActivity(), PhotoDetailActivity()::class.java)
        intent.putExtra(PHOTO_ID, photo.id)
        startActivity(intent)
    }
    private fun fabOnClick() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivityForResult(intent, backToMainActivity)
    }
}