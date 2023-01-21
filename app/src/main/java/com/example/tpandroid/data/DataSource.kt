/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tpandroid.data

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tpandroid.App
import kotlinx.coroutines.*

/* Handles operations on photosLiveData and holds details about it. */
class DataSource(resources: Resources) {

    private val _uiScope = MainScope()

    /* Returns photo given an ID from the database. */
    fun getPhotosList(): Result<LiveData<List<Urls>>?> {
        var photos: List<Urls>
        var result: LiveData<List<Urls>>? = null

        return try {
            runBlocking {
                val fetchDatabaseTask = _uiScope.launch(Dispatchers.IO) {
                    Log.println(
                        Log.DEBUG,
                        "DATABASE",
                        "PHOTOS LIST VIEW MODEL ENTER INTO DATABASE OPERATIONS THREAD"
                    )
                    photos = App.database.photosRepository().getAll()
                    result = MutableLiveData(photos)
                    Log.println(Log.DEBUG, "DATABASE", "PHOTOS LIST VIEW FILLED")
                }

                fetchDatabaseTask.join()

            }
            Log.println(Log.DEBUG, "DATABASE", "PHOTOS LIST VIEW MODEL IS BACK INTO MAIN THREAD")

            Result.success(result)

        } catch (e: java.lang.Exception) {
            Log.println(Log.WARN, "DATABASE", e.message.toString())
            Result.failure(e)
        }
    }


    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}