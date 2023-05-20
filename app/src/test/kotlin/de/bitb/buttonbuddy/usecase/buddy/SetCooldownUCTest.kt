package de.bitb.buttonbuddy.usecase.buddy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.getMessageString
import de.bitb.buttonbuddy.core.misc.DEFAULT_COOLDOWN
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.calculateMilliseconds
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.usecase.buddies.SetCooldownUC
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
class SetCooldownUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepo: UserRepository
    private lateinit var mockBuddyRepo: BuddyRepository
    private lateinit var setCooldownUC: SetCooldownUC

    private lateinit var testBuddy: Buddy

    private val testHours: Int = 4
    private val testMins: Int = 2

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepo = mockk()
        mockBuddyRepo = mockk()
        setCooldownUC = SetCooldownUC(mockUserRepo, mockBuddyRepo)

        testBuddy = buildBuddy()
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(buildUser())
        coEvery { mockBuddyRepo.saveCooldown(any(), any()) } returns Resource.Success()
    }

    @Test
    fun `get user error, should return error`() = runTest {
        val expectedError = "Database Error".asResourceError<User?>()
        coEvery { mockUserRepo.getLocalUser() } returns expectedError

        val errorResp = setCooldownUC(testBuddy, testHours, testMins)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `No user found, should return error`() = runTest {
        coEvery { mockUserRepo.getLocalUser() } returns Resource.Success(null)

        val errorResp = setCooldownUC(testBuddy, testHours, testMins)
        assert(errorResp is Resource.Error)
        assertEquals(
            R.string.user_not_found.asResourceError<User>().getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `save cooldown failed, should return error`() = runTest {
        val expectedError = "Save Cooldown Error".asResourceError<Unit>()
        coEvery { mockBuddyRepo.saveCooldown(any(), any()) } returns expectedError

        val errorResp = setCooldownUC(testBuddy, testHours, testMins)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `cooldown saved, should return success`() = runTest {
        val resultCooldown = calculateMilliseconds(testHours, testMins)
        assert(testBuddy.cooldown == DEFAULT_COOLDOWN)

        val successResp = setCooldownUC(testBuddy, testHours, testMins)
        assert(successResp is Resource.Success)
        coVerify {
            mockBuddyRepo.saveCooldown(
                any(),
                match { it.cooldown == resultCooldown },
            )
        }
    }
}
