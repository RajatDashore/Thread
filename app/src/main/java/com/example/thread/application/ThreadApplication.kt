package com.example.thread.application

import android.app.Application
//import com.cloudinary.android.MediaManager

class ThreadApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()
        config["cloud_name"] = "dkwe2soel"
        config["api_key"] = "354486887716225"
        config["api_secret"] = "ZOYER0BOeb8oXi_lToiRmaD5Ooc"
    }
}