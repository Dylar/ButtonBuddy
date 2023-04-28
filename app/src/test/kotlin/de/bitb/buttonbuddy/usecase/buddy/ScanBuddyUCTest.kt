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
        mockBuddyRepo = mockk()
        scanBuddyUC = ScanBuddyUC(mockUserRepo, mockBuddyRepo)

        testUser = buildUser()
        testBuddy = buildBuddy()
        coEvery { mockUserRepo.isUserLoggedIn() } returns Resource.Success(true)
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(testUser)
        coEvery { mockBuddyRepo.loadBuddies(any(), listOf(testBuddy.uuid)) } returns
                Resource.Success(listOf(testBuddy))
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(testUser)
    }

    @Test
    fun `get user error, should return error`() = runTest {
        val expectedError = "Database Error".asResourceError<User?>()
        coEvery { mockUserRepo.getLocalUser() } returns expectedError

        val errorResp = scanBuddyUC(testBuddy.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `load buddy error, should return error`() = runTest {
        val expectedError = "Load Buddy Error".asResourceError<List<Buddy>>()
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns expectedError

        val errorResp = scanBuddyUC(testBuddy.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `No buddy found, should return error`() = runTest {
        coEvery { mockBuddyRepo.loadBuddies(any(), any()) } returns Resource.Success(listOf())

        val errorResp = scanBuddyUC(testBuddy.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            R.string.no_buddy_found.asResourceError<List<Buddy>>().message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Save user error, should return error`() = runTest {
        val expectedError = "Save user error".asResourceError<User>()
        coEvery { mockUserRepo.saveUser(any()) } returns expectedError

        val errorResp = scanBuddyUC(testBuddy.uuid)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `Buddy scanned, should return success`() = runTest {
        val successResp = scanBuddyUC(testBuddy.uuid)
        assert(successResp is Resource.Success)
    }
}
