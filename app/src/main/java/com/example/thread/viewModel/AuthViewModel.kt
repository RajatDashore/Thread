package com.example.thread.viewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.thread.model.UserModel
import com.example.thread.utils.Constants
import com.example.thread.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

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
    fun register(
        email: String,
        pass: String,
        name: String,
        username: String,
        bio: String,
        imageUri: Uri,
        context: Context
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    _firebaseUser.postValue(auth.currentUser)

                    // 1. Save user without image first
                    val user = UserModel(email, pass, name, bio, username, "", uid)
                    usersRef.child(uid).setValue(user)

                    // 2. Upload image to Cloudinary and update DB
                    uploadImageToCloud(user, imageUri, context)
                    saveUserFcmToken()
                    subscribeUserToTopic()

                    Toast.makeText(context, "Registration Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    _error.postValue(it.exception?.message)
                }
            }
        }

    }

    // 🔹 Upload image to Cloudinary & update only imageUri in Firebase
    private fun uploadImageToCloud(userData: UserModel, imageUri: Uri, context: Context) {
        MediaManager.get().upload(imageUri)
            .unsigned("Threads") // unsigned preset from Cloudinary
            .option("folder", "Users/${userData.uid}/ProfileImage")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // optional: progress tracking
                }

                @OptIn(UnstableApi::class)
                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val imageUrl = resultData["secure_url"] as? String ?: ""
                    Log.d("$TAG-Success", imageUrl)

                    // update only the imageUri field
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
                        }

                    _uploadState.value = true
                }

                @OptIn(UnstableApi::class)
                override fun onError(requestId: String, error: ErrorInfo) {
                    _uploadState.value = false
                    Log.d("$TAG-Error", error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
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


    @OptIn(UnstableApi::class)
    fun saveUserFcmToken() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            val database = FirebaseDatabase.getInstance().reference
            database.child(Constants.USERS).child(uid).child("Token").setValue(token)
        }
    }

    @OptIn(UnstableApi::class)
    fun subscribeUserToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("global")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "User subscribed to global topic")
                } else {
                    Log.e("FCM", "Subscription failed", task.exception)
                }
            }
    }


    // 🔹 Logout user
    fun logout() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }
}
