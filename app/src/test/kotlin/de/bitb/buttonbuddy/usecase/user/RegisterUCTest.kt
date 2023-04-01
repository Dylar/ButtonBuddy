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

    private val pw1 = "password"
    private val pw2 = "password"

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

        coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns Resource.Success(Unit)
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(user)

        // When
        val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

        // Then
        assert(actualResp is Resource.Success)
        assert(actualResp.data is RegisterResponse.Registered)
    }

    @Test
    fun `given invalid first name, when invoke registerUser, then return FirstNameEmpty response`() =
        runTest {
            // Given
            val user = buildUser().copy(firstName = "")
            val expectedResp = RegisterResponse.FirstNameEmpty().asError

            // When
            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            // Then
            assert(actualResp is Resource.Error)
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
            val expectedResp = RegisterResponse.LastNameEmpty().asError

            // When
            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            // Then
            assert(actualResp is Resource.Error)
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
            val expectedResp = RegisterResponse.UserNameEmpty().asError

            // When
            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            // Then
            assert(actualResp is Resource.Error)
            assertEquals(
                expectedResp.message!!.asString(::getString),
                actualResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `when password is not the same, then returns error`() = runTest {
        val user = buildUser()

        val expectedError = RegisterResponse.PWNotSame().asError
        val actualResp = registerUC(user.firstName, user.lastName, user.userName, "pw1", "pw2")

        assert(actualResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            actualResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `when UserRepository returns error while registering user, then returns error`() =
        runTest {
            val user = buildUser()

            val expectedError = Resource.Error<Unit>("DATABASE_ERROR")
            coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns expectedError

            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            assert(actualResp is Resource.Error)
            assertEquals(expectedError.message, actualResp.message)
            coVerify { mockUserRepo.registerUser(user.userName, pw1) }
        }

    @Test
    fun `when UserRepository returns error while saving user, then returns error`() =
        runTest {
            val user = buildUser()

            val expectedError = Resource.Error<User>("DATABASE_ERROR")
            coEvery { mockUserRepo.registerUser(user.userName, pw1) } returns Resource.Success(Unit)
            coEvery { mockUserRepo.saveUser(any()) } returns expectedError

            val actualResp = registerUC(user.firstName, user.lastName, user.userName, pw1, pw2)

            assert(actualResp is Resource.Error)
            assertEquals(expectedError.message, actualResp.message)
            coVerifyOrder {
                mockUserRepo.registerUser(user.userName, pw1)
                mockUserRepo.saveUser(any())
            }
        }

}
