package com.example.tpandroid.data

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root[] root = om.readValue(myJsonString, Root[].class); */
class Links {
    var self: String? = null
    var html: String? = null
    var download: String? = null
    var download_location: String? = null
    var photos: String? = null
    var likes: String? = null
    var portfolio: String? = null
    var following: String? = null
    var followers: String? = null
}