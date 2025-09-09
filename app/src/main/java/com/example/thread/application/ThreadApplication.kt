package com.example.thread.application

import android.app.Application
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.cloudinary.android.MediaManager
import okhttp3.OkHttpClient


class ThreadApplication : Application() {

    companion object {
        lateinit var imageLoader: ImageLoader
    }

    override fun onCreate() {
        super.onCreate()

        //  Cloudinary data for image saving...
        val config = mapOf(
            "cloud_name" to "dkwe2soel",
            "api_key" to "354486887716225",
            "api_secret" to "ZOYER0BOeb8oXi_lToiRmaD5Ooc"
        )

        try {
            MediaManager.init(this, config)
        } catch (e: Exception) {
            println("Cloudinary initialisation failed -> ${e.message}")
        }

        val okHttpClient = OkHttpClient.Builder().build()
        imageLoader = ImageLoader.Builder(this@ThreadApplication)

            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED).memoryCache {
                MemoryCache.Builder().maxSizePercent(this@ThreadApplication, 0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED).diskCache {
                DiskCache.Builder().maxSizePercent(.03).directory(cacheDir.resolve("Image_Cache"))
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }


    fun ClearAllCacheImages() {
        imageLoader.diskCache?.clear()
        imageLoader.memoryCache?.clear()
    }
}
