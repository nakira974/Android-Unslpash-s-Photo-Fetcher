package com.example.tpandroid.data

import java.util.*

data class Photo(
    var id: String?,
    var created_at: Date?,
    var updated_at: Date?,
    var promoted_at: Date?,
    var width: Int? = 0,
    var height: Int = 0,
    var color: String?,
    var blur_hash: String?,
    var description: String?,
    var alt_description: String?,
    var urls: Urls?,
    var links: Links?,
    var likes: Int = 0,
    var liked_by_user: Boolean = false,
    var current_user_collections: ArrayList<Any>?,
    var sponsorship: Sponsorship?,
    var topic_submissions: TopicSubmissions?,
    var user: User? = null
)