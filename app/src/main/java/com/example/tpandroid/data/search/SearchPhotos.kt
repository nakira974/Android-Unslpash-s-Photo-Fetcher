package com.example.tpandroid.data.search

data class SearchPhotos(
    val results: List<Result>,
    val total: Int,
    val total_pages: Int
)