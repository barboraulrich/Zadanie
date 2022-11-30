package com.example.zadanie.helpers

import android.content.Context
import android.content.SharedPreferences
import com.example.zadanie.data.api.UserResponse
import com.google.gson.Gson


class PreferenceData private constructor() {

    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUserItem(context: Context?, userItem: UserResponse?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        userItem?.let {
            editor.putString(userKey, Gson().toJson(it))
            editor.apply()
            return
        }
        editor.putString(userKey, null)
        editor.apply()
    }

    fun getUserItem(context: Context?): UserResponse? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val json = sharedPref.getString(userKey, null) ?: return null

        return Gson().fromJson(json, UserResponse::class.java)
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenceData? = null

        fun getInstance(): PreferenceData =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: PreferenceData().also { INSTANCE = it }
            }

        private const val shpKey = "mobvPrefData"
        private const val userKey = "userKey"
    }
}