package de.bitb.buttonbuddy.data.source

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceDatabase(private val pref: SharedPreferences) : TokenDao, SettingsDao {

    override fun setToken(token: String) = pref.edit().putString("token", token).apply()
    override fun getToken(): String = pref.getString("token", "")!!

    override fun setDarkMode(isDark: Boolean) = pref.edit().putBoolean("darkMode", isDark).apply()
    override fun getDarkMode(): Boolean = pref.getBoolean("darkMode", true)

    override fun setCoolDowns(cooldowns: Map<String, Long>) {
        val json = Gson().toJson(cooldowns)
        pref.edit().putString("cooldowns", json).apply()
    }

    override fun getCoolDowns(): Map<String, Long> {
        val json = pref.getString("cooldowns", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<Map<String, Long>>() {}.type)
        } else {
            mapOf()
        }
    }
}