package de.bitb.buttonbuddy.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.SettingsRepository
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
class LoadDataUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockSettingsRepo: SettingsRepository
    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockBuddyRepo: BuddyRepository
    private lateinit var loadDataUC: LoadDataUC

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSettingsRepo = mockk()
        coEvery { mockSettingsRepo.loadSettings(any()) } returns Resource.Success(mapOf())

        mockUserRepo = mockk()
        mockBuddyRepo = mockk()
        loadDataUC = LoadDataUC(mockSettingsRepo, mockUserRepo, mockBuddyRepo)
    }

    @Test
    fun `user not logged in, should return error`() = runTest {
        val expectedError = "User not logged in".asResourceError<Boolean>()
        coEvery { mockUserRepo.isUserLoggedIn() } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `get user error, should return error`() = runTest {
        val expectedError = "Database Error".asResourceError<User?>()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `no user found, should return error`() = runTest {
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(null)

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            R.string.user_not_found.asResourceError<User?>().message!!.asString(::getString)
        )
    }

    @Test
    fun `load user error, should return error`() = runTest {
        val expectedError = "Load User Error".asResourceError<User?>()
        val user = buildUser()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockUserRepo.loadUser(any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `load buddys error, should return error`() = runTest {
        val expectedError = "Load Buddys Error".asResourceError<List<Buddy>>()
        val user = buildUser(mutableListOf("uuidX"))
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockUserRepo.loadUser(any()) } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `User has no buddys and load settings error, should return error`() = runTest {
        val expectedError = "Load Settings Error".asResourceError<Map<String, Long>>()
        val user = buildUser()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockUserRepo.loadUser(any()) } returns Resource.Success(user)
        coEvery { mockSettingsRepo.loadSettings(any()) } returns expectedError

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
        coVerify(exactly = 0) { mockBuddyRepo.loadBuddies(any(), any()) }
    }

    @Test
    fun `Load data complete, should return success`() = runTest {
        val user = buildUser(mutableListOf("uuidX"))
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockUserRepo.loadUser(any()) } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns
                Resource.Success(listOf(buildBuddy()))
        coEvery { mockSettingsRepo.loadSettings(any()) } returns Resource.Success()

        val errorResp = loadDataUC()
        assert(errorResp is Resource.Success)
        assert(errorResp.hasData)
        assert(errorResp.data == true)
    }
}
