package com.example.thread.viewModel

import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.thread.model.ThreadModel
import com.example.thread.utils.Constants
import com.example.thread.utils.sendBroadCastNotification
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AddThreadViewModel : ViewModel() {

    private var _isPosted = MutableLiveData<Boolean>()
    var isPosted: LiveData<Boolean> = _isPosted
    private val db = FirebaseDatabase.getInstance()

    private val _isUploading = MutableLiveData(false)
    val isUploading: LiveData<Boolean> = _isUploading
    val usersRef = db.getReference("Users")
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: MutableLiveData<FirebaseUser?> = _firebaseUser


    fun uploadThread(imageUri: Uri, userName: String, thread: String, userId: String) {
        if (_isUploading.value == true) {
            return
        }
        MediaManager.get().upload(imageUri)
            .unsigned("Threads") // unsigned preset from Cloudinary
            .option("folder", "Users/$userId/Threads") // 🔹 organized folder structure
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                    _isUploading.postValue(true)
                }

                @UnstableApi
                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val imageUrl = resultData["secure_url"] as? String ?: ""
                    val publicId = resultData["public_id"] as? String ?: ""
                    saveImageToFireBase(thread, userId, imageUrl)
                    sendBroadCastNotification("Thread", "$userName has posted something")
                    _isUploading.postValue(false)
                    _isPosted.postValue(true)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    _isPosted.postValue(false)
                    _isUploading.postValue(false)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {

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

