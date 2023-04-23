package de.bitb.buttonbuddy.data.model

data class Settings(
    val isDarkMode: Boolean = true,
    val buddysCooldown: Map<String, Long> = mutableMapOf()
)

