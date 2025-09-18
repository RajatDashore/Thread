package com.example.thread.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.model.CommentModel
import com.example.thread.model.CommentWithUser
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel
import com.example.thread.model.UserWithThreads
import com.example.thread.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference(Constants.USERS)

    private val _userThreads = MutableStateFlow<List<UserWithThreads>>(emptyList())
    val userThreads: StateFlow<List<UserWithThreads>> = _userThreads

    private val _userComment = MutableStateFlow<List<CommentWithUser>>(emptyList())
    val userComment: StateFlow<List<CommentWithUser>> = _userComment

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        fetchUsersAndThreads()
    }

    fun fetchUsersAndThreads() {
        _isLoading.value = true

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { child ->
                    val userId = child.key ?: return@mapNotNull null
                    val user = child.getValue(UserModel::class.java)?.copy(uid = userId)

                    if (user != null) {
                        val threadsSnapshot = child.child(Constants.THREADS)
                        val threads = threadsSnapshot.children.mapNotNull { threadSnap ->
                            val thread = threadSnap.getValue(ThreadModel::class.java)
                            thread?.copy(
                                threadId = threadSnap.key ?: "",
                                userId = user.uid,
                                Likes = threadSnap.child(Constants.LIKES).children.associate {
                                    it.key!! to (it.getValue(Boolean::class.java) ?: false)
                                },
                                Comments = threadSnap.child(Constants.COMMENTS).children.associate { snap ->
                                    snap.key!! to (snap.getValue(CommentModel::class.java)
                                        ?: CommentModel())
                                }
                            )
                        }

                        UserWithThreads(user, threads)
                    } else null
                }

                _userThreads.value = users
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }


    fun toggleLike(threadId: String, otherUserId: String, currentUserId: String) {
        viewModelScope.launch {
            val likeRef =
                usersRef.child(otherUserId).child(Constants.THREADS).child(threadId)
                    .child(Constants.LIKES)
                    .child(currentUserId)
            val snapshot = likeRef.get().await()
            if (snapshot.exists()) {
                likeRef.removeValue().await()
            } else {
                likeRef.setValue(true).await()
            }

        }
    }


    fun addComment(otherUid: String, currentUid: String, threadId: String, comment: String) {
        viewModelScope.launch {

            val commentRef = usersRef.child(otherUid).child(Constants.THREADS).child(threadId)
                .child(Constants.COMMENTS).push()

            val comment = CommentModel(
                commentId = commentRef.key,
                userId = currentUid,
                text = comment,
                System.currentTimeMillis().toString()
            )
            commentRef.setValue(comment).await()
        }
    }


    fun fetchComment(otherUid: String, threadId: String) {
        val commentRef = usersRef.child(otherUid).child(Constants.THREADS).child(threadId).child(
            Constants.COMMENTS
        )

        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allComments = snapshot.children.mapNotNull { commentSnap ->
                    val comment = commentSnap.getValue(CommentModel::class.java)
                        ?.copy(commentId = commentSnap.key)

                    comment?.let {
                        val userSnap = usersRef.child(it.userId ?: "")
                        var user: UserModel? = null
                        userSnap.get().addOnSuccessListener { snap ->
                            user = snap.getValue(UserModel::class.java)
                            _userComment.value = _userComment.value + CommentWithUser(user, it)
                        }
                    }
                }


            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}
