package com.example.thread.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.model.ThreadModel
import com.example.thread.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostProfileViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val userRef = db.getReference(Constants.USERS)
    private val _post = MutableStateFlow<List<ThreadModel>>(emptyList())
    val post: StateFlow<List<ThreadModel>> = _post


    fun getAllPost() {
        viewModelScope.launch {
            userRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(Constants.THREADS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val threadList = mutableListOf<ThreadModel>()
                        snapshot.children.forEach { child ->
                            val thread = child.getValue(ThreadModel::class.java)
                            thread.let {
                                threadList.add(it!!)
                            }
                        }
                        _post.value = threadList

                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
        }
    }

}