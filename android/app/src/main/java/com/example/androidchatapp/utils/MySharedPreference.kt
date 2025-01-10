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

    fun getAccessToken(context: Context): String {
        val preferences: SharedPreferences =
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preferences.getString("accessToken", "").toString()
    }

    fun removeAccessToken(context: Context) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        preferences.edit().remove("accessToken").apply()
    }

    fun setContactsSave(context: Context) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        preferences.edit().putBoolean("contactsSave", true).apply()
    }

    fun hasSaveContacts(context: Context): Boolean {
        val preferences: SharedPreferences =
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preferences.getBoolean("contactsSave", false)
    }
}