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

const val ZERO_CHAR = 0.toChar()
private const val CHARSET_NAME = "UTF-8"
private const val ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding"
private const val IV_LENGTH = 12
private const val SALT_LENGTH = 16
private const val TAG_LENGTH = 128
private const val KEY_LENGTH = 256
private const val ITERATION_COUNT = 65536

val ENCRYPTION_EXCEPTION = "Encryption failed".asResourceError<String>()
val DECRYPTION_EXCEPTION = "Decryption failed".asResourceError<String>()

// Encrypts the provided data string using the given key
suspend fun encrypt(data: String, key: CharArray): Resource<String> {
    return tryIt(
        onError = { ENCRYPTION_EXCEPTION },
        onTry = {
            val salt = generateSecureRandomBytes(SALT_LENGTH)
            val secretKey = deriveKey(key, salt)
            clearKey(key)

            val iv = generateSecureRandomBytes(IV_LENGTH)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)

            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

            // Encrypt the data by converting it to bytes and applying the encryption algorithm
            val encryptedData = cipher.doFinal(data.toByteArray(Charset.forName(CHARSET_NAME)))

            // Combine the salt, IV, and encrypted data into a single byte array
            val combined = ByteArray(salt.size + iv.size + encryptedData.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(encryptedData, 0, combined, salt.size + iv.size, encryptedData.size)

            // Encode the combined byte array as a UrlBase64 string
            Resource.Success(Base64.getUrlEncoder().encodeToString(combined))
        },
    )
}

// Decrypts the provided encrypted data string using the given key
suspend fun decrypt(data: String, key: CharArray): Resource<String> {
    return tryIt(
        onError = { DECRYPTION_EXCEPTION },
        onTry = {
            // Decode the UrlBase64-encoded input data
            val combined = Base64.getUrlDecoder().decode(data)

            // Extract the salt, IV, and encrypted data from the byte array
            val salt = combined.copyOfRange(0, SALT_LENGTH)
            val iv = combined.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
            val encryptedData = combined.copyOfRange(SALT_LENGTH + IV_LENGTH, combined.size)

            val secretKey = deriveKey(key, salt)
            clearKey(key)

            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            val decryptedData = cipher.doFinal(encryptedData)

            // Convert the decrypted byte array back to a string using the specified character set
            val decryptedString = String(decryptedData, Charset.forName(CHARSET_NAME))
            Resource.Success(decryptedString)
        },
    )
}

// Derives a secret key from the provided key and salt using PBKDF2 with HMAC-SHA256
private fun deriveKey(key: CharArray, salt: ByteArray): SecretKey {
    val keySpec = PBEKeySpec(key, salt, ITERATION_COUNT, KEY_LENGTH)
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val keyBytes = secretKeyFactory.generateSecret(keySpec).encoded
    return SecretKeySpec(keyBytes, "AES")
}

// Generates a specified number of secure random bytes
private fun generateSecureRandomBytes(length: Int): ByteArray =
    ByteArray(length).apply { SecureRandom.getInstanceStrong().nextBytes(this) }

// Clears the sensitive key material stored in the provided character array
private fun clearKey(key: CharArray) = key.forEachIndexed { i, _ -> key[i] = ZERO_CHAR }
