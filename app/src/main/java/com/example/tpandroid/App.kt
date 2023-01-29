package com.example.tpandroid

import android.app.Application
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.room.Room

class App : MultiDexApplication() {

    companion object {
        lateinit var database: ApplicationDbContext
        const val databaseName : String = "photos.dat"
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(this, ApplicationDbContext::class.java, databaseName)
                .build()
        Log.println(Log.DEBUG, "DATABASE", "ROOM INSTANCE HAS BEEN CREATED")
    }

    override fun onTerminate() {
        super.onTerminate()
        database.close()
        Log.println(Log.DEBUG, "DATABASE", "ROOM INSTANCE HAS BEEN DISPOSED")

    }


}