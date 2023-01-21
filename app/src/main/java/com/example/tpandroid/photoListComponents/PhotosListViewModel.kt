package com.example.tpandroid.photoListComponents

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tpandroid.data.DataSource
import com.example.tpandroid.data.Urls
import kotlinx.coroutines.MainScope

class PhotosListViewModel(val dataSource: DataSource) : ViewModel() {

    private val _uiScope = MainScope()

    val photosLiveData: Result<LiveData<List<Urls>>?>
        get() = dataSource.getPhotosList()
}

class PhotosListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotosListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotosListViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}