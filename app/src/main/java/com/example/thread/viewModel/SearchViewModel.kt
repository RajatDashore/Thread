package com.example.thread.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thread.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    val users = db.getReference("Users")


    private var _users = MutableLiveData<List<UserModel>>()
    val usersList: LiveData<List<UserModel>> = _users


    init {
        FetchUsers {
            _users.value = it
        }
    }

    private fun FetchUsers(onResult: (List<UserModel>) -> Unit) {
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<UserModel>()
                for (threadsnapshot in snapshot.children) {
                    val thread = threadsnapshot.getValue(UserModel::class.java)
                    if (thread?.uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                        continue
                    }

                    result.add(thread!!)
                }
                onResult(result)
            }


            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}

