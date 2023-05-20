package de.bitb.buttonbuddy.core.misc

import java.util.*
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding"
private const val IV_LENGTH = 12
private const val TAG_LENGTH = 128
private const val SALT_LENGTH = 16
private const val CHARSET_NAME = "UTF-8"
const val ZERO_CHAR = 0.toChar()

suspend fun encrypt(data: String, key: CharArray): Resource<String> {
    return tryIt(
        onTry = {
            val salt = generateSecureRandomBytes(SALT_LENGTH)
            val secretKey = deriveKey(key, salt)
            clearKey(key)
            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            val iv = generateSecureRandomBytes(IV_LENGTH)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
            val encryptedData = cipher.doFinal(data.toByteArray(Charset.forName(CHARSET_NAME)))
            val combined = ByteArray(salt.size + iv.size + encryptedData.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(encryptedData, 0, combined, salt.size + iv.size, encryptedData.size)
            return@tryIt Resource.Success(Base64.getUrlEncoder().encodeToString(combined))
        },
        onError = { "Encryption failed".asResourceError() },
    )
}

suspend fun decrypt(data: String, key: CharArray): Resource<String> {
    return tryIt(
        onTry = {
            val combined = Base64.getUrlDecoder().decode(data)
            val salt = combined.copyOfRange(0, SALT_LENGTH)
            val iv = combined.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
            val encryptedData = combined.copyOfRange(SALT_LENGTH + IV_LENGTH, combined.size)
            val secretKey = deriveKey(key, salt)
            clearKey(key)
            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            val decryptedData = cipher.doFinal(encryptedData)
            return@tryIt Resource.Success(String(decryptedData, Charset.forName(CHARSET_NAME)))
        },
        onError = { "Decryption failed".asResourceError() },
    )
}

private fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
    val keySpec = PBEKeySpec(password, salt, 65536, 256)
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val keyBytes = secretKeyFactory.generateSecret(keySpec).encoded
    return SecretKeySpec(keyBytes, "AES")
}

private fun generateSecureRandomBytes(length: Int): ByteArray {
    val randomBytes = ByteArray(length)
    SecureRandom.getInstanceStrong().nextBytes(randomBytes)
    return randomBytes
}

private fun clearKey(key: CharArray) {
    key.forEachIndexed { i, _ -> key[i] = ZERO_CHAR }
}