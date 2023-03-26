package de.bitb.buttonbuddy.data.source

import android.content.SharedPreferences

class PreferenceDatabase(private val pref: SharedPreferences) : TokenDao, SettingsDao {

    override fun setToken(token: String) = pref.edit().putString("token", token).apply()
    override fun getToken(): String = pref.getString("token", "")!!

    override fun setCoolDown(cd: Long) = pref.edit().putLong("cooldown", cd).apply()
    override fun getCoolDown(): Long = pref.getLong("cooldown", 1000 * 60 * 60 * 2)

    override fun setDarkMode(isDark: Boolean) = pref.edit().putBoolean("darkMode", isDark).apply()
    override fun getDarkMode(): Boolean = pref.getBoolean("darkMode", true)

}