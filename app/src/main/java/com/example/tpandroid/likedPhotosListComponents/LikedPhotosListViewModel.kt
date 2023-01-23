package com.example.tpandroid.likedPhotosListComponents

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tpandroid.data.DataSource
import com.example.tpandroid.data.Urls
import kotlinx.coroutines.MainScope

class LikedPhotosListViewModel(val dataSource: DataSource) : ViewModel() {

    private val _uiScope = MainScope()

    val likedPhotosLiveData: Result<LiveData<List<Urls>>?>
        get() = dataSource.getLikedPhotosList()
}

class LikedPhotosListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikedPhotosListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LikedPhotosListViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}