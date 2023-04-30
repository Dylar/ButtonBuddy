package de.bitb.buttonbuddy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildMessage
import de.bitb.buttonbuddy.shared.buildUser
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MsgRepoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRemoteService: RemoteService
    private lateinit var mockLocalDBMock: LocalDatabase
    private lateinit var msgRepo: MessageRepository

    private lateinit var testUser: User
    private lateinit var testBuddy: Buddy
    private lateinit var testMsg: Message

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRemoteService = mockk()
        mockLocalDBMock = mockk()
        msgRepo = MessageRepositoryImpl(mockRemoteService, mockLocalDBMock)

        testUser = buildUser()
        testBuddy = buildBuddy()
        testMsg = buildMessage()
    }

    @Test
    fun `get last msg failed, should return error`() = runTest {
        val expectedError = Exception("Last msg error")
        coEvery { mockLocalDBMock.getLastMessage(any()) } throws expectedError

        val errorResp = msgRepo.getLastMessage(testBuddy.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `get last msg succeeded, should return error`() = runTest {
        coEvery { mockLocalDBMock.getLastMessage(any()) } returns testMsg

        val successResp = msgRepo.getLastMessage(testBuddy.uuid)
        assert(successResp is Resource.Success)
        assertTrue(successResp.hasData)
        assertEquals(successResp.data, testMsg)
    }

    @Test
    fun `save last msg failed, should return error`() = runTest {
        val expectedError = Exception("Save msg error")
        coEvery { mockLocalDBMock.insert(any<Message>()) } throws expectedError

        val errorResp = msgRepo.saveMessage(testMsg)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `save last msg succeeded, should return error`() = runTest {
        coJustRun { mockLocalDBMock.insert(any<Message>()) }

        val successResp = msgRepo.saveMessage(testMsg)
        assert(successResp is Resource.Success)
    }
}
