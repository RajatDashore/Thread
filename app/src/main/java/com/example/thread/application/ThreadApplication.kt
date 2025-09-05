package com.example.thread.application

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // ✅ Cloudinary Config
        val config = mapOf(
            "cloud_name" to "dkwe2soel",   // 🔹 replace with your Cloudinary cloud name
            "api_key" to "354486887716225",   // optional if you need signed uploads
            "api_secret" to "ZOYER0BOeb8oXi_lToiRmaD5Ooc" // optional for signed uploads
        )

        try {
            MediaManager.init(this, config)
            println("✅ Cloudinary initialized successfully")
        } catch (e: Exception) {
            println("❌ Cloudinary init failed: ${e.message}")
        }
    }
}
