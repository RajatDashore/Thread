package com.example.thread.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thread.model.UserModel
import com.example.thread.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val usersRef = db.getReference("Users")

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: MutableLiveData<FirebaseUser?> = _firebaseUser


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _firebaseUser.value = auth.currentUser
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
                TODO("Not yet implemented")
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

        usersRef.child(uid!!).setValue(userData).addOnSuccessListener {
            SharedPref.storeData(name, email, bio, username, imageUri, context)
        }.addOnFailureListener {

        }
    }


    fun logout() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }
}

