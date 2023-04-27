package de.bitb.buttonbuddy.usecase.buddy

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

@OptIn(ExperimentalCoroutinesApi::class)
class ScanBuddyUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockBuddyRepo: BuddyRepository
    private lateinit var scanBuddyUC: ScanBuddyUC

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepo = mockk()
        mockBuddyRepo = mockk()
        scanBuddyUC = ScanBuddyUC(mockUserRepo, mockBuddyRepo)
    }

    @Test
    fun `get user error, should return error`() = runTest {
        val user = buildUser()
        val expectedError = "Database Error".asResourceError<User?>()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns expectedError

        val errorResp = scanBuddyUC(user.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `load buddy error, should return error`() = runTest {
        val user = buildUser()
        val expectedError = "Load Buddy Error".asResourceError<List<Buddy>>()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns expectedError

        val errorResp = scanBuddyUC(user.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `No buddy found, should return error`() = runTest {
        val user = buildUser()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns Resource.Success(listOf())

        val errorResp = scanBuddyUC(user.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            R.string.no_buddy_found.asResourceError<List<Buddy>>().message!!.asString(::getString)
        )
    }

    @Test
    fun `Save user error, should return error`() = runTest {
        val user = buildUser()
        val buddy = buildBuddy()
        val expectedError = "Save user error".asResourceError<User>()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns Resource.Success(listOf(buddy))
        coEvery { mockUserRepo.saveUser(any()) } returns expectedError

        val errorResp = scanBuddyUC(user.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            errorResp.message!!.asString(::getString),
            expectedError.message!!.asString(::getString)
        )
    }

    @Test
    fun `Buddy scanned, should return success`() = runTest {
        val user = buildUser()
        val buddy = buildBuddy()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(user)
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns Resource.Success(listOf(buddy))
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(user)

        val successResp = scanBuddyUC(user.uuid)
        assert(successResp is Resource.Success)
    }
}
