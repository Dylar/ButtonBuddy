package de.bitb.buttonbuddy.core

import de.bitb.buttonbuddy.core.misc.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class EncryptionTest {

    private fun checkKeys(
        keys: List<CharArray> = arrayListOf(),
        usedKeys: List<CharArray> = arrayListOf()
    ) {
        keys.forEach { key -> assert(key.any { it != ZERO_CHAR }) }
        usedKeys.forEach { key -> assert(key.all { it == ZERO_CHAR }) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `encrypt - decrypt`() = runTest {
        val data = "what the fuck data to encrypt"
        val key = "superSecret!!38467$1&§%!)326OhneDasGehtsNicht"
        val key1 = key.toCharArray()
        val key2 = key.toCharArray()
        val key3 = key.toCharArray()
        val key4 = key.toCharArray()
        checkKeys(keys = arrayListOf(key1, key2, key3, key4))

        val encryptedData1 = encrypt(data, key1).data!!
        checkKeys(
            keys = arrayListOf(key2, key3, key4),
            usedKeys = arrayListOf(key1)
        )
        val encryptedData2 = encrypt(data, key2).data!!
        checkKeys(
            keys = arrayListOf(key3, key4),
            usedKeys = arrayListOf(key1, key2)
        )
        val decryptedData1 = decrypt(encryptedData1, key3).data!!
        checkKeys(
            keys = arrayListOf(key4),
            usedKeys = arrayListOf(key1, key2, key3)
        )
        val decryptedData2 = decrypt(encryptedData1, key4).data!!
        checkKeys(usedKeys = arrayListOf(key1, key2, key3, key4))
        val decryptedData3 = decrypt(encryptedData1, "just wrong".toCharArray())

        assertNotEquals(data, encryptedData1)
        assertNotEquals(data, encryptedData2)
        assertNotEquals(encryptedData1, decryptedData1)
        assertNotEquals(encryptedData1, decryptedData2)
        assertNotEquals(encryptedData1, encryptedData2)
        assertNotEquals(encryptedData2, decryptedData1)
        assertNotEquals(encryptedData2, decryptedData2)

        assertEquals(data, decryptedData1)
        assertEquals(data, decryptedData2)
        assertNotEquals(data, decryptedData3.data)
        assert(decryptedData3 is Resource.Error)
        assertEquals(decryptedData3, DECRYPTION_EXCEPTION)
    }
}
