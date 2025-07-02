package com.example.thread.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thread.model.ThreadModel
import com.google.android.gms.cast.tv.media.MediaManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AddThreadViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    val usersRef = db.getReference("Threads")

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()


    private var _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted


    fun saveImage(
        thread: String,
        uid: String?,
        imageUri: String
    ) {
        val threadData = ThreadModel(thread, imageUri, uid, System.currentTimeMillis().toString())

        usersRef.child(usersRef.push().key!!).setValue(threadData).addOnSuccessListener {
            _isPosted.postValue(true)
        }.addOnFailureListener {
            _isPosted.postValue(false)
        }

        /* MediaManager.upload(imageUri)
             .option("uid", uid) // Use a meaningful public ID
             .option("thread", thread)
             .option("timestamp", System.currentTimeMillis().toString())// Use a meaningful public ID
             .dispatch()

         */

    }

    fun saveData(
        thread: String,
        uid: String?,
        imageUri: String,
    ) {

        val threadData = ThreadModel(thread, imageUri, uid, System.currentTimeMillis().toString())

        usersRef.child(usersRef.push().key!!).setValue(threadData).addOnSuccessListener {
            _isPosted.postValue(true)
        }.addOnFailureListener {
            _isPosted.postValue(false)
        }
    }


}

