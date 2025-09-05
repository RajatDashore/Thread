package com.example.thread.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel
import com.example.thread.model.UserWithThreads
import com.example.thread.utils.Constants
import com.google.firebase.database.FirebaseDatabase

class HomeViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference(Constants.USERS)

    private val _userThreads = MutableLiveData<List<UserWithThreads>>()
    val userThreads: LiveData<List<UserWithThreads>> = _userThreads

    fun fetchUsersAndThreads() {
        getAllUsers { users ->
            getThreadsForUsers(users) { userWithThreadsList ->
                _userThreads.value = userWithThreadsList
            }
        }
    }

    private fun getAllUsers(onResult: (List<UserModel>) -> Unit) {
        usersRef.get().addOnSuccessListener { snapshot ->
            val users = snapshot.children.mapNotNull { child ->
                val userId = child.key ?: return@mapNotNull null
                child.getValue(UserModel::class.java)
            }
            onResult(users)
        }.addOnFailureListener {
            onResult(emptyList())
        }
    }

    private fun getThreadsForUsers(
        users: List<UserModel>,
        onResult: (List<UserWithThreads>) -> Unit
    ) {
        val resultList = mutableListOf<UserWithThreads>()
        var completed = 0

        users.forEach { user ->
            usersRef.child(user.uid.toString()).child(Constants.THREADS)
                .get().addOnSuccessListener { snapshot ->
                    val threads =
                        snapshot.children.mapNotNull { it.getValue(ThreadModel::class.java) }
                    resultList.add(UserWithThreads(user, threads))
                    completed++
                    if (completed == users.size) {
                        onResult(resultList)
                    }
                }.addOnFailureListener {
                    completed++
                    if (completed == users.size) {
                        onResult(resultList)
                    }
                }
        }
    }
}
