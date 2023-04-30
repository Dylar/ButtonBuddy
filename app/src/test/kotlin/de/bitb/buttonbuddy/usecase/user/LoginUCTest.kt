package de.bitb.buttonbuddy.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildBuddy
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
class LoginUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockSettingsRepo: SettingsRepository
    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockBuddyRepo: BuddyRepository
    private lateinit var loginUC: LoginUC

    private lateinit var testUser: User
    private lateinit var testBuddy: Buddy

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSettingsRepo = mockk()
        mockUserRepo = mockk()
        mockBuddyRepo = mockk()
        loginUC = LoginUC(mockSettingsRepo, mockUserRepo, mockBuddyRepo)

        testBuddy = buildBuddy()
        testUser = buildUser(buddies = mutableListOf(testBuddy.uuid))
        coEvery { mockSettingsRepo.loadSettings(any()) } returns Resource.Success(mapOf())
        coEvery { mockUserRepo.loginUser(testUser.email, any()) } returns Resource.Success(testUser)
        coEvery { mockUserRepo.saveUser(testUser) } returns Resource.Success(testUser)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns
                Resource.Success(listOf(testBuddy))
    }

    @Test
    fun `login with valid credentials, should return success`() = runTest {
        val pw = "validPassword"
        val user = buildUser()
        val expectedError = "WRONG PW".asResourceError<User?>()
        coEvery { mockUserRepo.loginUser(user.email, any()) } returns expectedError
        coEvery { mockUserRepo.loginUser(user.email, pw) } returns Resource.Success(user)
        coEvery { mockUserRepo.saveUser(user) } returns Resource.Success(user)

        val errorResp = loginUC(user.email, "wrongPassword")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.ErrorThrown<*>)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )

        val successResp = loginUC(user.email, pw)
        assert(successResp is Resource.Success)
        assert(successResp.data is LoginResponse.LoggedIn)
    }

    @Test
    fun `login with empty user name should return error`() = runTest {
        val errorResp = loginUC("", "pw")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.EmailEmpty)
    }

    @Test
    fun `login with empty password should return error`() = runTest {
        val errorResp = loginUC(testUser.email, "")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.PwEmpty)
    }

    @Test
    fun `login with non-existing user should return user not found error`() = runTest {
        coEvery { mockUserRepo.loginUser(testUser.email, "pw") } returns Resource.Success(null)
        val errorResp = loginUC(testUser.email, "pw")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.UserNotFound)
    }

    @Test
    fun `login, load buddies and return buddies not loaded error`() = runTest {
        val expectedError = "Load Buddies error".asResourceError<List<Buddy>>()
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns expectedError

        val errorResp = loginUC(testUser.email, "pw")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.ErrorThrown<*>)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `login, load buddies and return success`() = runTest {
        val successResp = loginUC(testUser.email, "pw")
        assert(successResp is Resource.Success)
        assert(successResp.data is LoginResponse.LoggedIn)
        coVerify { mockBuddyRepo.loadBuddies(any(), any()) }
    }
}
