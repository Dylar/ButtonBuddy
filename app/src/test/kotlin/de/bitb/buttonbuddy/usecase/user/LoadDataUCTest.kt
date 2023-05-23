package de.bitb.buttonbuddy.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.getMessageString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildMessage
import de.bitb.buttonbuddy.shared.buildUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoadDataUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockSettingsRepo: SettingsRepository
    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockBuddyRepo: BuddyRepository
    private lateinit var mockMsgRepo: MessageRepository
    private lateinit var loadDataUC: LoadDataUC

    private lateinit var testUser: User

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepo = mockk()
        mockBuddyRepo = mockk()
        mockMsgRepo = mockk()
        mockSettingsRepo = mockk()
        loadDataUC = LoadDataUC(mockSettingsRepo, mockUserRepo, mockBuddyRepo, mockMsgRepo)

        testUser = buildUser()
        coEvery { mockSettingsRepo.loadSettings(any()) } returns Resource.Success(mapOf())
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(testUser)
        coEvery { mockUserRepo.loadUser(any()) } returns Resource.Success(testUser)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns
                Resource.Success(listOf(buildBuddy()))
        coEvery { mockMsgRepo.loadMessages(any()) } returns
                Resource.Success(listOf(buildMessage()))
        coEvery { mockSettingsRepo.loadSettings(any()) } returns Resource.Success()
    }

    @Test
    fun `user not logged in, should return error`() = runTest {
        val expectedError = "User not logged in".asResourceError<Boolean>()
        coEvery { mockUserRepo.isUserLoggedIn() } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `get user error, should return error`() = runTest {
        val expectedError = "Database Error".asResourceError<User?>()
        coEvery { mockUserRepo.getLocalUser() } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `no user found, should return error`() = runTest {
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(null)

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            R.string.user_not_found.asResourceError<User?>().getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `load user error, should return error`() = runTest {
        val expectedError = "Load User Error".asResourceError<User?>()
        coEvery { mockUserRepo.loadUser(any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `load buddys error, should return error`() = runTest {
        val expectedError = "Load Buddys Error".asResourceError<List<Buddy>>()
        val user = buildUser(mutableListOf("uuidX"))
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockUserRepo.loadUser(any()) } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `load messages error, should return error`() = runTest {
        val expectedError = "Load Messages Error".asResourceError<List<Message>>()
        coEvery { mockMsgRepo.loadMessages(any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `User has no buddys and load settings error, should return error`() = runTest {
        val expectedError = "Load Settings Error".asResourceError<Map<String, Long>>()
        coEvery { mockSettingsRepo.loadSettings(any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
        coVerify(exactly = 0) { mockBuddyRepo.loadBuddies(any(), any()) }
    }

    @Test
    fun `Load data complete, should return success`() = runTest {
        val successResp = loadDataUC()
        assert(successResp is Resource.Success)
        assert(successResp.hasData)
        assert(successResp.data == true)
    }
}
