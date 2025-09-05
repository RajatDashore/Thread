package com.example.thread.viewModel

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.thread.model.UserModel
import com.example.thread.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthViewModel : ViewModel() {
    private val TAG = "AUTH_VIEW_MODEL"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference("Users")

    private val _uploadState = MutableLiveData<Boolean>()
    val uploadState: LiveData<Boolean> = _uploadState

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _firebaseUser.value = auth.currentUser
    }

    // 🔹 Register user
    @OptIn(UnstableApi::class)
    fun register(
        email: String,
        pass: String,
        name: String,
        username: String,
        bio: String,
        imageUri: String,
        context: Context
    ) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                _firebaseUser.postValue(auth.currentUser)

                // 1. Save user to Firebase without image first
                val user = UserModel(email, pass, name, bio, username, "", uid)
                usersRef.child(uid).setValue(user)
                Log.d(TAG + 4, imageUri)

                // 2. Upload image to Cloudinary (will update imageUri later)
                uploadImageToCloud(user.copy(imageUri = imageUri), context)

                Toast.makeText(context, "Registration Successfully", Toast.LENGTH_SHORT).show()
            } else {
                _error.postValue(it.exception?.message)
            }
        }
    }

    // 🔹 Upload image to Cloudinary & update only imageUri in Firebase
    fun uploadImageToCloud(userData: UserModel, context: Context) {
        MediaManager.get().upload(userData.imageUri)
            .unsigned("Threads") // unsigned preset
            .option("folder", "Users/${userData.uid}/ProfileImage")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                }

                @OptIn(UnstableApi::class)
                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val imageUrl = resultData["secure_url"] as? String ?: ""

                    Log.d(TAG + 1, imageUrl)

                    usersRef.child(userData.uid!!).child("imageUri")
                        .setValue(imageUrl)
                        .addOnSuccessListener {
                            SharedPref.storeData(
                                userData.name ?: "",
                                userData.email ?: "",
                                userData.bio ?: "",
                                userData.username ?: "",
                                imageUrl,
                                context
                            )
                            Log.d(TAG + 2, imageUrl)
                        }

                    _uploadState.value = true
                }

                @OptIn(UnstableApi::class)
                override fun onError(requestId: String, error: ErrorInfo) {
                    _uploadState.value = false
                    Log.d(TAG + 3, error.toString())
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {

                }
            }).dispatch()
    }

    // 🔹 Login user
    fun login(email: String, pass: String, context: Context) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val uid = auth.currentUser?.uid
                getData(uid, context)
                _firebaseUser.postValue(auth.currentUser)
            } else {
                _error.postValue(it.exception?.message)
            }
        }
    }

    private fun getData(uid: String?, context: Context) {
        if (uid == null) return
        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserModel::class.java)
                if (userData != null) {
                    SharedPref.storeData(
                        userData.name ?: "",
                        userData.email ?: "",
                        userData.bio ?: "",
                        userData.username ?: "",
                        userData.imageUri ?: "",
                        context
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔹 Logout user
    fun logout() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }
}
