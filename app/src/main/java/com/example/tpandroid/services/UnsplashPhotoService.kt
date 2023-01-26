package com.example.tpandroid.services

import com.example.tpandroid.data.Photo
import com.example.tpandroid.data.search.SearchPhotos
import retrofit2.Call
import retrofit2.http.*

interface UnsplashPhotoService {
    @GET("/photos/random?count=11&topics=nature")
    fun getRandomPhotos(@Header("Authorization") token: String): Call<List<Photo>>

    @POST("/photos/{image_id}/like")
    fun postLikePhoto(
        @Header("Authorization") token: String,
        @Path("image_id") image_id: String
    ): Call<Photo>

    @GET("/search/photos")
    fun getSearchPhotos(
        @Header("Authorization") token: String,
        @Query(value = "query" ) user_query: String
    ): Call<SearchPhotos>

    @DELETE("/photos/{image_id}/like")
    fun deleteLikePhoto(
        @Header("Authorization") token: String,
        @Path("image_id") image_id: String
    ): Call<Photo>
}