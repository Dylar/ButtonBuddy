package de.bitb.buttonbuddy.usecase.message

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.DEFAULT_COOLDOWN
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.data.source.MessageService
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildMessage
import de.bitb.buttonbuddy.shared.buildSettings
import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class SendMessageUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockSettingsRepo: SettingsRepository
    private lateinit var mockMsgRepo: MessageRepository
    private lateinit var mockMsgService: MessageService
    private lateinit var sendMessageUC: SendMessageUC

    private lateinit var testSettings: Settings
    private lateinit var testUser: User
    private lateinit var testBuddy: Buddy

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepo = mockk()
        mockSettingsRepo = mockk()
        mockMsgRepo = mockk()
        mockMsgService = mockk()
        sendMessageUC = SendMessageUC(mockMsgService, mockSettingsRepo, mockUserRepo, mockMsgRepo)

        testSettings = buildSettings()
        testUser = buildUser()
        testBuddy = buildBuddy()
        coEvery { mockMsgRepo.getLastMessage(any()) } returns Resource.Success(null)
        coEvery { mockSettingsRepo.getSettings() } returns Resource.Success(testSettings)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(testUser)
        coEvery { mockMsgService.sendMessage(any()) } returns Resource.Success()
        coEvery { mockMsgRepo.saveMessage(any()) } returns Resource.Success()
    }

    @Test
    fun `get last msg error, should return error`() = runTest {
        val expectedError = "Database Error".asResourceError<Message>()
        coEvery { mockMsgRepo.getLastMessage(any()) } returns expectedError

        val errorResp = sendMessageUC(testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Load settings failed, should return error`() = runTest {
        val expectedError = "Load Settings Error".asResourceError<Settings>()
        coEvery { mockSettingsRepo.getSettings() } returns expectedError

        val errorResp = sendMessageUC(testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `On Cooldown, should return error`() = runTest {
        coEvery { mockMsgRepo.getLastMessage(any()) } returns Resource.Success(buildMessage())

        val errorResp = sendMessageUC(testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            R.string.send_on_cooldown.asResourceError<Unit>().message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Get user failed, should return error`() = runTest {
        val msg = buildMessage(date = Date(System.currentTimeMillis() - DEFAULT_COOLDOWN - 1))
        val expectedError = "Load User Error".asResourceError<User?>()
        coEvery { mockMsgRepo.getLastMessage(any()) } returns Resource.Success(msg)
        coEvery { mockUserRepo.getLocalUser() } returns expectedError

        val errorResp = sendMessageUC(testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Send msg failed, should return error`() = runTest {
        val expectedError = "Send Message Error".asResourceError<Unit>()
        coEvery { mockMsgService.sendMessage(any()) } returns expectedError

        val errorResp = sendMessageUC(testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Save msg failed, should return error`() = runTest {
        val expectedError = "Save Message Error".asResourceError<Unit>()
        coEvery { mockMsgRepo.saveMessage(any()) } returns expectedError

        val errorResp = sendMessageUC(testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Send msg complete, should return success`() = runTest {
        val successResp = sendMessageUC(buildBuddy())
        assert(successResp is Resource.Success)
    }
}
