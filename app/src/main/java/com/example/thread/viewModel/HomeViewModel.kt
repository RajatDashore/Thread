package com.example.thread.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel
import com.example.thread.model.UserWithThreads
import com.example.thread.utils.Constants
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference(Constants.USERS)

    private val _userThreads = MutableStateFlow<List<UserWithThreads>>(emptyList())
    val userThreads: StateFlow<List<UserWithThreads>> = _userThreads

    fun fetchUsersAndThreads() {
        viewModelScope.launch {
            try {
                // 1. Fetch all users
                val usersSnapshot = usersRef.get().await()
                val users = usersSnapshot.children.mapNotNull { child ->
                    val userId = child.key ?: return@mapNotNull null
                    val user = child.getValue(UserModel::class.java)
                    user?.copy(uid = userId) // make sure uid is set
                }

                // 2. Fetch threads for each user
                val resultList = users.map { user ->
                    val threadsSnapshot = usersRef
                        .child(user.uid.toString())
                        .child(Constants.THREADS)
                        .get()
                        .await()

                    val threads = threadsSnapshot.children.mapNotNull {
                        it.getValue(ThreadModel::class.java)
                    }
                    UserWithThreads(user, threads)
                }

                // 3. Update StateFlow
                _userThreads.value = resultList

            } catch (e: Exception) {
                e.printStackTrace()
                _userThreads.value = emptyList()
            }
        }
    }
}
