package de.bitb.buttonbuddy.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
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
        val firstName = "John"
        val lastName = "Doe"
        val userName = "johndoe"
        val pw1 = "password1"
        val pw2 = "password1"
        val user = User(firstName, lastName, userName, UUID.randomUUID().toString())
        val expectedResp = Resource.Success(RegisterResponse.Registered())

        coEvery { mockUserRepo.registerUser(userName, pw1) } returns Resource.Success(Unit)
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(user)

        // When
        val actualResp = registerUC(firstName, lastName, userName, pw1, pw2)

        // Then
        assertEquals(expectedResp, actualResp)
    }

    @Test
    fun `given invalid first name, when invoke registerUser, then return FirstNameEmpty response`() =
        runTest {
            // Given
            val firstName = ""
            val lastName = "Doe"
            val userName = "johndoe"
            val pw1 = "password1"
            val pw2 = "password1"
            val expectedResp = RegisterResponse.FirstNameEmpty().asError

            // When
            val actualResp = registerUC(firstName, lastName, userName, pw1, pw2)

            // Then
            assertEquals(expectedResp, actualResp)
        }

    @Test
    fun `given invalid last name, when invoke registerUser, then return LastNameEmpty response`() =
        runTest {
            // Given
            val firstName = "John"
            val lastName = ""
            val userName = "johndoe"
            val pw1 = "password1"
            val pw2 = "password1"
            val expectedResp = RegisterResponse.LastNameEmpty().asError

            // When
            val actualResp = registerUC(firstName, lastName, userName, pw1, pw2)

            // Then
            assertEquals(expectedResp, actualResp)
        }

    @Test
    fun `given invalid username, when invoke registerUser, then return UserNameEmpty response`() =
        runTest {
            // Given
            val firstName = "John"
            val lastName = "Doe"
            val userName = ""
            val pw1 = "password1"
            val pw2 = "password1"
            val expectedResp = RegisterResponse.UserNameEmpty().asError

            // When
            val actualResp = registerUC(firstName, lastName, userName, pw1, pw2)

            // Then
            assertEquals(expectedResp, actualResp)
        }

    @Test
    fun `when UserRepository throws exception while registering user, then returns error`() =
        runTest {
            val firstName = "John"
            val lastName = "Doe"
            val userName = "johndoe"
            val pw1 = "password"
            val pw2 = "password"

            val expectedError =
                RegisterResponse.ErrorThrown(Resource.Error<RegisterResponse>("DATABASE_ERROR"))
            coEvery { mockUserRepo.registerUser(userName, pw1) } throws Exception()

            val result = registerUC(firstName, lastName, userName, pw1, pw2)

            assertEquals(expectedError, result)
            coVerify { mockUserRepo.registerUser(userName, pw1) }
        }

    @Test
    fun `when UserRepository throws exception while saving user, then returns error`() =
        runTest {
            val firstName = "John"
            val lastName = "Doe"
            val userName = "johndoe"
            val pw1 = "password"
            val pw2 = "password"

            val expectedError =
                RegisterResponse.ErrorThrown(Resource.Error<RegisterResponse>("DATABASE_ERROR"))
            coEvery { mockUserRepo.saveUser(any()) } throws Exception()

            val result = registerUC(firstName, lastName, userName, pw1, pw2)

            assertEquals(expectedError, result)
            coVerifyOrder {
                mockUserRepo.registerUser(userName, pw1)
                mockUserRepo.saveUser(any())
            }
        }

    @Test
    fun `when user information is valid, then returns success`() = runTest {
        val firstName = "John"
        val lastName = "Doe"
        val userName = "johndoe"
        val pw1 = "password"
        val pw2 = "password"
        val user = User(firstName, lastName, userName, UUID.randomUUID().toString())

        val expectedSuccess = Resource.Success(RegisterResponse.Registered())
        coEvery { mockUserRepo.registerUser(userName, pw1) } returns Resource.Success(Unit)
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(user)

        val result = registerUC(firstName, lastName, userName, pw1, pw2)

        assertEquals(expectedSuccess, result)
        coVerifyOrder {
            mockUserRepo.registerUser(userName, pw1)
            mockUserRepo.saveUser(any())
        }
    }
}
