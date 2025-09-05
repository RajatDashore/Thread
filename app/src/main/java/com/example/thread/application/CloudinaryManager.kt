package com.example.thread.application

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.UploadStatus
import com.example.thread.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CloudinaryManager(context: Context) {



    init {
        try {
            val config = mapOf(
                "cloud_name" to "dkwe2soel"
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
            println("Cloudinary init failed${e.message}")
        }
    }



    fun resetState() {
        //_uploadState.value = UploadState.Idle
    }

    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        data class Progress(val percentage: Int) : UploadState()
        data class Success(val imageUrl: String, val publicId: String) : UploadState()
        data class Error(val message: String) : UploadState()
    }

}