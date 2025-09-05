package com.example.thread.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.thread.application.CloudinaryManager.UploadState
import com.example.thread.model.ThreadModel
import com.example.thread.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddThreadViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private var _isPosted = MutableLiveData<Boolean>()
    var isPosted: LiveData<Boolean> = _isPosted
    private val db = FirebaseDatabase.getInstance()
    val usersRef = db.getReference("Users")
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: MutableLiveData<FirebaseUser?> = _firebaseUser

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uplaodState: StateFlow<UploadState> = _uploadState


    fun uploadThread(imageUri: Uri, thread: String, userId: String) {
        _uploadState.value = UploadState.Loading
        MediaManager.get().upload(imageUri)
            .unsigned("Threads") // unsigned preset from Cloudinary
            .option("folder", "Users/$userId/Threads") // 🔹 organized folder structure
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                    _uploadState.value = UploadState.Progress(progress)
                }

                @UnstableApi
                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val imageUrl = resultData["secure_url"] as? String ?: ""
                    val publicId = resultData["public_id"] as? String ?: ""

                    saveImageToFireBase(thread, userId, imageUrl)

                    _uploadState.value = UploadState.Success(imageUrl, publicId)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    _uploadState.value = UploadState.Error(error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    _uploadState.value = UploadState.Error("Rescheduled: ${error.description}")
                }
            }).dispatch()
    }

    @OptIn(UnstableApi::class)
    private fun saveImageToFireBase(thread: String, userId: String?, imageUri: String) {
        val threadData =
            ThreadModel(thread, imageUri, userId, System.currentTimeMillis().toString())
        usersRef.child(userId!!).child(Constants.THREADS).push().setValue(threadData)
            .addOnCompleteListener {

            }.addOnFailureListener {

            }

    }


}

