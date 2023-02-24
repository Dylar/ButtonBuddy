package de.bitb.buttonbuddy.data.source

import android.content.SharedPreferences

interface PreferenceDatabase {
    fun saveToken(token: String)
    fun getToken(): String
}

class PreferenceDatabaseImpl(private val pref: SharedPreferences) : PreferenceDatabase {

    override fun saveToken(token: String) = pref.edit().putString("token", token).apply()

    override fun getToken(): String = pref.getString("token", "")!!
}