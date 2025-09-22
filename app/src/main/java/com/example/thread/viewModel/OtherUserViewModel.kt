package com.example.thread.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.model.NotificationModel
import com.example.thread.model.OtherUserModel
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel
import com.example.thread.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OtherUserViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val thread = db.getReference("Threads")
    private val userRef = db.getReference("Users")

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

    private var userModel = UserModel()


    fun followToggle(
        otherUid: String,
        otherUserName: String,
        date: String,
        context: Context
    ) {
        viewModelScope.launch {
            val otherUser = OtherUserModel(otherUid, otherUserName, date)

            val refFollowing =
                userRef.child(currentUserUid!!).child(Constants.FOLLOWING).child(otherUid)
            val snapshot = refFollowing.get().await()

            val refFollower =
                userRef.child(otherUid).child(Constants.FOLLWERS).child(currentUserUid)
            val snapshot2 = refFollower.get().await()

            if (snapshot.exists() && snapshot2.exists()) {

                refFollowing.removeValue().await()
                refFollower.removeValue().await()
                _isFollowing.postValue(false)

                Toast.makeText(
                    context,
                    "You have unfollowed $otherUserName",
                    Toast.LENGTH_SHORT
                ).show()

                sendNotificationByFirebase(
                    otherUid,
                    "${userModel.name} has unfollowed you",
                    userModel.imageUri!!
                )

                /*sendNotificationToOneUser(
                    getToken(otherUid)!!,
                    "Thread",
                    "${userModel.name} has unfollowed you"
                )

                 */
            } else {
                refFollowing.setValue(true).await()
                refFollower.setValue(true).await()
                _isFollowing.postValue(true)
                sendNotificationByFirebase(
                    otherUid,
                    "${userModel.name} has followed you",
                    userModel.imageUri!!
                )

                Toast.makeText(
                    context,
                    "You are now following $otherUserName",
                    Toast.LENGTH_SHORT
                ).show()

                /*  sendNotificationToOneUser(
                      getToken(otherUid)!!,
                      "Thread",
                      "${userModel.name} has started following you"
                  )
                 */
            }
        }
    }


    fun sendNotificationByFirebase(otherUid: String, message: String, image: String) {
        val notification = NotificationModel(
            FirebaseAuth.getInstance().currentUser!!.uid,
            message,
            System.currentTimeMillis().toString(),
            image
        )
        FirebaseDatabase.getInstance().getReference(Constants.USERS).child(otherUid)
            .child(Constants.NOTIFICATION).push().setValue(notification)
    }


    private fun getToken(otherUid: String): String? {
        userRef.child(otherUid).child(Constants.TOKEN).get().addOnSuccessListener { snapshot ->
            val token = snapshot.getValue(String::class.java)
            if (token != null) {
                return@addOnSuccessListener
            }
        }
        return ""
    }


    fun fetchFollowersAndFollowingCounts(otherUid: String) {
        viewModelScope.launch {
            try {
                val followingRef = userRef.child(otherUid).child(Constants.FOLLOWING)
                val snapshot = followingRef.get().await()
                snapshot.children.mapNotNull { count ->
                    _followingCount.postValue(snapshot.childrenCount.toInt())
                }
                val followerRef = userRef.child(otherUid).child(Constants.FOLLWERS)
                val snapshot2 = followerRef.get().await()
                snapshot2.children.mapNotNull { count ->
                    _followerCount.postValue(snapshot2.childrenCount.toInt())
                }
            } catch (e: Exception) {
                _followerCount.postValue(0)
                _followingCount.postValue(0)
            }
        }
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


    fun checkUserStatus(otherUid: String) {
        viewModelScope.launch {
            val ref = userRef.child(currentUserUid!!).child(Constants.FOLLOWING).child(otherUid)
            val snapshot = ref.get().await()
            _isFollowing.postValue(snapshot.exists()) // true if following
        }
    }


}

