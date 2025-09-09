package com.example.thread.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.model.OtherUserModel
import com.example.thread.model.ThreadModel
import com.example.thread.utils.Constants
import com.example.thread.utils.sendNotificationToOneUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtherUserViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val thread = db.getReference("Threads")
    private val userRef = db.getReference("Users")
    private val followersRef = db.getReference("Followers")
    private val followingRef = db.getReference("Following")
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    // LiveData for follower and following counts
    private val _followerCount = MutableLiveData<Int>()
    val followerCount: MutableLiveData<Int> = _followerCount

    private val _followingCount = MutableLiveData<Int>()

    private val _otherUserPost = MutableStateFlow<List<ThreadModel>>(emptyList())
    val otherUserPost: StateFlow<List<ThreadModel>> = _otherUserPost
    val followingCount: MutableLiveData<Int> = _followingCount


    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: MutableLiveData<Boolean> = _isFollowing


    fun doFollow(uid: String, otherUserName: String, date: String, context: Context) {

        val otherUser = OtherUserModel(uid, otherUserName, date)

        followingRef.child(currentUserUid!!).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(context, "Already following", Toast.LENGTH_SHORT).show()
                    } else {
                        // Add to 'Following' of current user
                        followingRef.child(currentUserUid!!).child(uid)
                            .setValue(otherUser)
                            .addOnSuccessListener {
                                // Add to 'Followers' of the target user
                                val followerUser = OtherUserModel(currentUserUid, "You", date)
                                followersRef.child(uid).child(currentUserUid)
                                    .setValue(followerUser)
                                    .addOnSuccessListener {
                                        val token: String = getToken()!!
                                        sendNotificationToOneUser(
                                            token,
                                            "Thread",
                                            "One user has started following you"
                                        )
                                        Toast.makeText(
                                            context,
                                            "You have started following $otherUserName",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }

                private fun getToken(): String? {
                    userRef.child(uid).child("Token").get().addOnSuccessListener { snapshot ->
                        val token = snapshot.getValue(String::class.java)
                        if (token != null) {
                            return@addOnSuccessListener
                        }
                    }
                    return ""
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    fun checkUserStatus(uid: String) {
        followingRef.child(currentUserUid!!).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _isFollowing.value = snapshot.exists()
                }

                override fun onCancelled(error: DatabaseError) {
                    _isFollowing.value = false
                }
            })
    }


    fun fetchFollowersAndFollowingCounts(uid: String) {
        // Fetch follower count
        followersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _followerCount.value = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                _followerCount.value = 0
            }
        })

        // Fetch following count
        followingRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _followingCount.value = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                _followingCount.value = 0
            }
        })
    }


    fun getUserPost(uid: String) {
        viewModelScope.launch {
            userRef.child(uid).child(Constants.THREADS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val threadList = mutableListOf<ThreadModel>()
                        snapshot.children.forEach { child ->
                            val thread = child.getValue(ThreadModel::class.java)
                            thread.let {
                                threadList.add(it!!)
                            }
                        }
                        _otherUserPost.value = threadList

                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
        }
    }


}

