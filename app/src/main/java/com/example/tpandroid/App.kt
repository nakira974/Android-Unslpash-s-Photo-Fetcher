package com.example.tpandroid

import android.app.Application
import androidx.room.Room

class App : Application() {

    companion object {
        lateinit var database: ApplicationDbContext
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(this, ApplicationDbContext::class.java, "recyclersample.dat")
                .build()


    }


}