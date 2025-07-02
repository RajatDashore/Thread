package com.example.thread.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

object SharedPref {


    fun storeData(
        name: String,
        email: String,
        bio: String,
        userName: String,
        imageUri: String,
        context: Context
    ) {
        val sharedPreferances = context.getSharedPreferences("Users", MODE_PRIVATE)
        val editor = sharedPreferances.edit()
        editor.putString("Name", name)
        editor.putString("Email", email)
        editor.putString("Bio", bio)
        editor.putString("UserName", userName)
        editor.putString("ImageUri", imageUri)
        editor.apply()

    }


    fun getUserName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Users", MODE_PRIVATE)
        return sharedPreferences.getString("UserName", "")!!
    }

    fun getName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Users", MODE_PRIVATE)
        return sharedPreferences.getString("Name", "")!!
    }

    fun getBio(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Users", MODE_PRIVATE)
        return sharedPreferences.getString("Bio", "")!!
    }

    fun getUserEmail(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Users", MODE_PRIVATE)
        return sharedPreferences.getString("Email", "")!!
    }

    fun getImage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Users", MODE_PRIVATE)
        return sharedPreferences.getString("ImageUri", "")!!
    }


}