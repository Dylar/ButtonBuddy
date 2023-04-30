package de.bitb.buttonbuddy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildBuddy
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BuddyRepoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRemoteService: RemoteService
    private lateinit var mockLocalDBMock: LocalDatabase
    private lateinit var buddyRepo: BuddyRepository

    private lateinit var testUser: User
    private lateinit var testBuddy: Buddy

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRemoteService = mockk()
        mockLocalDBMock = mockk()
        buddyRepo = BuddyRepositoryImpl(mockRemoteService, mockLocalDBMock)

        testUser = buildUser()
        testBuddy = buildBuddy()
    }

    @Test
    fun `load buddies failed, should return error`() = runTest {
        val expectedError = "Load buddies error".asResourceError<List<Buddy>>()
        coEvery { mockRemoteService.loadBuddies(any(), any()) } returns expectedError

        val errorResp = buddyRepo.loadBuddies(testUser.uuid, testUser.buddies)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
        coVerify(exactly = 0) { mockLocalDBMock.insertAll(any()) }
    }

    @Test
    fun `load buddies, insert buddies failed, should return error`() = runTest {
        val expectedError = Exception("Insert buddies error")
        coEvery { mockRemoteService.loadBuddies(any(), any()) } returns Resource.Success(emptyList())
        coEvery { mockLocalDBMock.insertAll(any()) } throws expectedError

        val errorResp = buddyRepo.loadBuddies(testUser.uuid, testUser.buddies)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `load buddies succeeded, should return error`() = runTest {
        coEvery { mockRemoteService.loadBuddies(any(), any()) } returns Resource.Success(emptyList())
        coJustRun { mockLocalDBMock.insertAll(any()) }

        val successResp = buddyRepo.loadBuddies(testUser.uuid, testUser.buddies)
        assert(successResp is Resource.Success)
    }

    @Test
    fun `save cooldown failed, should return error`() = runTest {
        val expectedError = "save cooldown error".asResourceError<Unit>()
        coEvery { mockRemoteService.updateCooldown(any(), any(), any()) } returns expectedError

        val errorResp = buddyRepo.saveCooldown(testUser.uuid, testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
        coVerify(exactly = 0) { mockLocalDBMock.insertAll(any()) }
    }

    @Test
    fun `save cooldown, insert buddies failed, should return error`() = runTest {
        val expectedError = Exception("Insert buddies error")
        coEvery { mockRemoteService.updateCooldown(any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { mockLocalDBMock.insertAll(any()) } throws expectedError

        val errorResp = buddyRepo.saveCooldown(testUser.uuid, testBuddy)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `save cooldown succeeded, should return error`() = runTest {
        coEvery { mockRemoteService.updateCooldown(any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { mockRemoteService.loadBuddies(any(), any()) } returns Resource.Success(emptyList())
        coJustRun { mockLocalDBMock.insertAll(any()) }

        val successResp = buddyRepo.loadBuddies(testUser.uuid, testUser.buddies)
        assert(successResp is Resource.Success)
    }
}
