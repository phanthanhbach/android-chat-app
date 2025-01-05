package com.example.androidchatapp.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPreference {
    private val fileName: String = "MY_SHARE_FILENAME"

    fun setAccessToken(context: Context, accessToken: String) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        preferences.edit().putString("accessToken", accessToken).apply()
    }
}