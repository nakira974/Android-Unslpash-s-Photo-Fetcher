package com.example.tpandroid.data

import java.util.*

class User {
    var id: String? = null
    var updated_at: Date? = null
    var username: String? = null
    var name: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var twitter_username: String? = null
    var portfolio_url: String? = null
    var bio: String? = null
    var location: String? = null
    var links: Links? = null
    var profile_image: ProfileImage? = null
    var instagram_username: String? = null
    var total_collections = 0
    var total_likes = 0
    var total_photos = 0
    var accepted_tos = false
    var for_hire = false
    var social: Social? = null
}