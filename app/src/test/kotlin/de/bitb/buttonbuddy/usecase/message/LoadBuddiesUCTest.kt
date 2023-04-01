package de.bitb.buttonbuddy.usecase.message

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.shared.buildBuddy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoadBuddiesUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockBuddyRepo: BuddyRepository
    private lateinit var loginUC: LoginUC

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepo = mockk()
        mockBuddyRepo = mockk()
        loginUC = LoginUC(mockUserRepo, mockBuddyRepo)
    }

    @Test
    fun `login with valid credentials, should return success`() = runTest {
        val pw = "validPassword"
        val user = buildUser()
        val expectedError = Resource.Error<User?>("WRONG PW")
        coEvery { mockUserRepo.loginUser(user.userName, any()) } returns expectedError
        coEvery { mockUserRepo.loginUser(user.userName, pw) } returns Resource.Success(user)
        coEvery { mockUserRepo.saveUser(user) } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any()) } returns Resource.Success(emptyList())

        val errorResp = loginUC(user.userName, "wrongPassword")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.ErrorThrown<*>)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )

        val actualResp = loginUC(user.userName, pw)
        assert(actualResp is Resource.Success)
        assert(actualResp.data is LoginResponse.LoggedIn)
    }

    @Test
    fun `login with empty user name should return error`() = runTest {
        val user = buildUser().copy(userName = "")

        val errorResp = loginUC(user.userName, "pw")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.UserEmpty)
    }

    @Test
    fun `login with empty password should return error`() = runTest {
        val user = buildUser()

        val errorResp = loginUC(user.userName, "")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.PwEmpty)
    }

    @Test
    fun `login with non-existing user should return user not found error`() = runTest {
        val user = buildUser()
        coEvery { mockUserRepo.loginUser(user.userName, "pw") } returns Resource.Success(null)

        val errorResp = loginUC(user.userName, "pw")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.UserNotFound)
    }

    @Test
    fun `login, load buddies and return buddies not loaded error`() = runTest {
        val user = buildUser(buddies = mutableListOf("uuid1"))
        val expectedError = Resource.Error<List<Buddy>>("Load Buddies error")
        coEvery { mockUserRepo.loginUser(user.userName, any()) } returns Resource.Success(user)
        coEvery { mockUserRepo.saveUser(user) } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any()) } returns expectedError

        val errorResp = loginUC(user.userName, "pw")
        assert(errorResp is Resource.Error)
        assert(errorResp.data is LoginResponse.ErrorThrown<*>)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `login, load buddies and return success`() = runTest {
        val buddy = buildBuddy()
        val user = buildUser(buddies = mutableListOf(buddy.uuid))
        coEvery { mockUserRepo.loginUser(user.userName, any()) } returns Resource.Success(user)
        coEvery { mockUserRepo.saveUser(user) } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any()) } returns Resource.Success(listOf(buddy))

        val actualResp = loginUC(user.userName, "pw")
        assert(actualResp is Resource.Success)
        assert(actualResp.data is LoginResponse.LoggedIn)
        coVerify { mockBuddyRepo.loadBuddies(any()) }
    }
}
