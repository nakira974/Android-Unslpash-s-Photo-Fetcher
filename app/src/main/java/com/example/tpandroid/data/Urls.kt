package com.example.tpandroid.data

import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Urls(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "raw_format_url")
    var raw: String?,
    @ColumnInfo(name = "full_format_url")
    var full: String?,
    @ColumnInfo(name = "regular_format_url")
    var regular: String?,
    @ColumnInfo(name = "small_format_url")
    var small: String?,
    @ColumnInfo(name = "thumb_format_url")
    var thumb: String?,
    @ColumnInfo(name = "small_s3_format_url")
    var small_s3: String?,
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "creator_name")
    var creator_name: String = "",
    @ColumnInfo(name = "download_url")
    var download_url: String = "",
    @ColumnInfo(name = "image_id")
    var image_id: String = "",
    @ColumnInfo(name = "is_cached")
    var is_cached: Boolean = false,
    @ColumnInfo(name="image_byteArray")
    var image_byteArray : ByteArray,
    @ColumnInfo(name="like_number")
    var like_number : Int
)