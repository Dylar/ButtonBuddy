package de.bitb.buttonbuddy.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.core.getString
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepo: UserRepository
    private lateinit var registerUC: RegisterUC

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepo = mockk()
        registerUC = RegisterUC(mockUserRepo)
    }

    @Test
    fun `given valid input, when invoke registerUser, then return Registered response`() = runTest {
        // Given
        val user = buildUser()
        val pw1 = "password1"
        val pw2 = "password1"

        coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns Resource.Success(Unit)
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(user)

        // When
        val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

        // Then
        assert(actualResp.data is RegisterResponse.Registered)
    }

    @Test
    fun `given invalid first name, when invoke registerUser, then return FirstNameEmpty response`() =
        runTest {
            // Given
            val user = buildUser().copy(firstName = "")
            val pw1 = "password1"
            val pw2 = "password1"
            val expectedResp = RegisterResponse.FirstNameEmpty().asError

            // When
            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            // Then
            assertEquals(
                expectedResp.message!!.asString(::getString),
                actualResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `given invalid last name, when invoke registerUser, then return LastNameEmpty response`() =
        runTest {
            // Given
            val user = buildUser().copy(lastName = "")
            val pw1 = "password1"
            val pw2 = "password1"
            val expectedResp = RegisterResponse.LastNameEmpty().asError

            // When
            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            // Then
            assertEquals(
                expectedResp.message!!.asString(::getString),
                actualResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `given invalid username, when invoke registerUser, then return UserNameEmpty response`() =
        runTest {
            // Given
            val user = buildUser().copy(userName = "")
            val pw1 = "password1"
            val pw2 = "password1"
            val expectedResp = RegisterResponse.UserNameEmpty().asError

            // When
            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            // Then
            assertEquals(
                expectedResp.message!!.asString(::getString),
                actualResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `when UserRepository throws exception while registering user, then returns error`() =
        runTest {
            val user = buildUser()
            val pw1 = "password"
            val pw2 = "password"

            val expectedError = Resource.Error<Unit>("DATABASE_ERROR")
            coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns expectedError

            val result = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            assertEquals(expectedError.message, result.message)
            coVerify { mockUserRepo.registerUser(user.userName, pw1) }
        }

    @Test
    fun `when UserRepository throws exception while saving user, then returns error`() =
        runTest {
            val user = buildUser()
            val pw1 = "password"
            val pw2 = "password"

            val expectedError = Resource.Error<User>("DATABASE_ERROR")
            coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns Resource.Success(Unit)
            coEvery { mockUserRepo.saveUser(any()) } returns expectedError

            val result = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            assertEquals(expectedError.message, result.message)
            coVerifyOrder {
                mockUserRepo.registerUser(user.userName, pw1)
                mockUserRepo.saveUser(any())
            }
        }

    @Test
    fun `when user information is valid, then returns success`() = runTest {
        val user = buildUser()
        val pw1 = "password"
        val pw2 = "password"

        coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns Resource.Success(Unit)
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(user)

        val result = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

        assert(result is Resource.Success)
        coVerifyOrder {
            mockUserRepo.registerUser(user.userName, pw1)
            mockUserRepo.saveUser(any())
        }
    }
}
