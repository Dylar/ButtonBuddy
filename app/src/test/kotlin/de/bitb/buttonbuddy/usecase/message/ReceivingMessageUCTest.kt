package de.bitb.buttonbuddy.usecase.message

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.Notifier
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
import io.mockk.*
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
class ReceivingMessageUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockMsgRepo: MessageRepository
    private lateinit var mockNotifier: Notifier
    private lateinit var receivingMessageUC: ReceivingMessageUC

    private lateinit var testMsg: Message

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockMsgRepo = mockk()
        mockNotifier = mockk()
        receivingMessageUC = ReceivingMessageUC(mockMsgRepo, mockNotifier)

        testMsg = buildMessage()
        coEvery { mockMsgRepo.saveMessage(any()) } returns Resource.Success()
        justRun { mockNotifier.showNotification(any()) }
    }

    @Test
    fun `Parsing msg fail, should return error`() = runTest {
        val errorResp = receivingMessageUC(mapOf())
        assert(errorResp is Resource.Error)
        assertEquals(
            NullPointerException().toString(),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Save msg fail, should return error`() = runTest {
        val expectedError = "Save Message Error".asResourceError<Unit>()
        coEvery { mockMsgRepo.saveMessage(any()) } returns expectedError
        val errorResp = receivingMessageUC(testMsg.toMap())
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Receive msg, should return success`() = runTest {
        val successResp = receivingMessageUC(testMsg.toMap())
        assert(successResp is Resource.Success)
        verify { mockNotifier.showNotification(any()) }
    }
}
