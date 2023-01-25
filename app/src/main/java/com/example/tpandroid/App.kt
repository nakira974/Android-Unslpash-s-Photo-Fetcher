package com.example.tpandroid

import android.app.Application
import android.util.Log
import androidx.room.Room

class App : Application() {

    companion object {
        lateinit var database: ApplicationDbContext
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(this, ApplicationDbContext::class.java, "photos.dat")
                .build()
        Log.println(Log.DEBUG, "DATABASE", "ROOM INSTANCE HAS BEEN CREATED")
    }

    override fun onTerminate() {
        super.onTerminate()
        database.close()
        Log.println(Log.DEBUG, "DATABASE", "ROOM INSTANCE HAS BEEN DISPOSED")

    }


}