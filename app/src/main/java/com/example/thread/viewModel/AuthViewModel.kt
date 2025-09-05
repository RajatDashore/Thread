package com.example.thread.viewModel

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.thread.application.CloudinaryManager.UploadState
import com.example.thread.model.UserModel
import com.example.thread.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val usersRef = db.getReference("Users")

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uplaodState: StateFlow<UploadState> = _uploadState

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: MutableLiveData<FirebaseUser?> = _firebaseUser


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _firebaseUser.value = auth.currentUser
    }

    fun uploadImageToCloud(userData: UserModel, context: Context) {
        _uploadState.value = UploadState.Loading
        MediaManager.get().upload(userData.imageUri)
            .unsigned("Threads") // unsigned preset from Cloudinary
            .option("folder", "Users/${userData.uid}/ThreadImage") // 🔹 organized folder structure
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
                    /*
                    email, pass, name, bio, username, imageUri, uid.toString()
                     */
                    var user = UserModel(
                        userData.email,
                        userData.pass,
                        userData.name,
                        userData.bio,
                        userData.username,
                        imageUrl,
                        userData.uid
                    )

                    usersRef.child(userData.uid!!).setValue(user).addOnSuccessListener {
                        SharedPref.storeData(
                            user.name.toString(),
                            user.email.toString(),
                            user.bio.toString(),
                            user.username.toString(),
                            imageUrl,
                            context
                        )

                    }.addOnFailureListener {

                    }

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


    fun login(email: String, pass: String, context: Context) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                getData(auth.currentUser?.uid, context)
                _firebaseUser.postValue(auth.currentUser)
            } else {
                _error.postValue(it.exception?.message)
            }
        }
    }

    private fun getData(uid: String?, context: Context) {
        usersRef.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserModel::class.java)
                if (userData != null) {
                    userData!!.email?.let {
                        userData.name?.let { it1 ->
                            userData.username?.let { it2 ->
                                userData.imageUri?.let { it3 ->
                                    userData!!.bio?.let { it4 ->
                                        SharedPref.storeData(
                                            it1,
                                            it,
                                            it4,
                                            it2,
                                            it3,
                                            context
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


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
                _firebaseUser.postValue(auth.currentUser)
                saveImage(
                    email,
                    pass,
                    name,
                    username,
                    bio,
                    imageUri,
                    auth.currentUser?.uid,
                    context
                )
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
            } else {
                _error.postValue(it.exception?.message)
            }
        }
    }

    private fun saveImage(
        email: String,
        pass: String,
        name: String,
        username: String,
        bio: String,
        imageUri: String,
        uid: String?,
        context: Context
    ) {
        saveData(email, pass, name, bio, username, imageUri, uid, context)
    }

    private fun saveData(
        email: String,
        pass: String,
        name: String,
        bio: String,
        username: String,
        imageUri: String,
        uid: String?,
        context: Context
    ) {
        val userData = UserModel(email, pass, name, bio, username, imageUri, uid.toString())
        uploadImageToCloud(userData, context)
    }


    fun logout() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }
}

