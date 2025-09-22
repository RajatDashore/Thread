package com.example.thread.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.model.NotificationModel
import com.example.thread.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val notRef = FirebaseDatabase.getInstance().getReference(Constants.USERS)
    private val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
    private val _notificationList = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notificationList: StateFlow<List<NotificationModel>> = _notificationList


    fun getAllNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notRef.child(currentUid).child(Constants.NOTIFICATION).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<NotificationModel>()
                    snapshot.children.forEach { child ->
                        val notification = child.getValue(NotificationModel::class.java)
                        notification.let {
                            list.add(it!!)
                        }
                    }
                    _notificationList.value = list
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

}