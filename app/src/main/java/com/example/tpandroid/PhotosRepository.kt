package com.example.tpandroid

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tpandroid.data.Urls

@Dao
interface PhotosRepository {
    @Query("SELECT * FROM photos")
    fun getAll(): List<Urls>

    @Query("SELECT * FROM photos WHERE is_cached = 0")
    fun getAllUnCached(): List<Urls>

    @Query("SELECT * FROM photos WHERE id IN (:photoIds)")
    fun loadAllByIds(photoIds: IntArray): List<Urls>

    @Query(
        "SELECT * FROM photos WHERE small_format_url LIKE :smallUrl AND " +
                "regular_format_url LIKE :regularUrl LIMIT 1"
    )
    fun findByName(smallUrl: String, regularUrl: String): Urls

    @Query("SELECT is_cached FROM photos WHERE id = :id")
    fun findByIdIfCached(id: Int): Boolean

    @Query("UPDATE photos SET is_cached = :is_cached WHERE id = :id")
    fun setCachedById(id: Int, is_cached: Boolean)

    @Insert
    fun insertAll(vararg flowers: Urls)

    @Delete
    fun delete(flower: Urls)

    @Query("DELETE FROM photos WHERE id = :id")
    fun deleteById(id: Int)
}