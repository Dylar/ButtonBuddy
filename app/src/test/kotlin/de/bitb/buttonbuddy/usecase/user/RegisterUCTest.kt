package de.bitb.buttonbuddy.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
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
class RegisterUCTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserRepo: UserRepository
    private lateinit var registerUC: RegisterUC

    private lateinit var testUser: User

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

        testUser = buildUser()
        coEvery { mockUserRepo.registerUser(testUser.email, pw1) } returns Resource.Success()
        coEvery { mockUserRepo.saveUser(any()) } returns Resource.Success(testUser)
    }

    @Test
    fun `given valid input, when invoke registerUser, then return Registered response`() = runTest {
        val successResp =
            registerUC(testUser.firstName, testUser.lastName, testUser.email, pw1, pw2)

        assert(successResp is Resource.Success)
        assert(successResp.data is RegisterResponse.Registered)
    }

    @Test
    fun `given invalid first name, when invoke registerUser, then return FirstNameEmpty response`() =
        runTest {
            val user = testUser.copy(firstName = "")
            val errorResp = registerUC(user.firstName, user.lastName, user.email, pw1, pw2)
            assert(errorResp is Resource.Error)
            assertEquals(
                RegisterResponse.FirstNameEmpty().asError.message!!.asString(::getString),
                errorResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `given invalid last name, when invoke registerUser, then return LastNameEmpty response`() =
        runTest {
            val user = testUser.copy(lastName = "")
            val expectedResp = RegisterResponse.LastNameEmpty().asError

            val errorResp = registerUC(user.firstName, user.lastName, user.email, pw1, pw2)
            assert(errorResp is Resource.Error)
            assertEquals(
                expectedResp.message!!.asString(::getString),
                errorResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `given invalid email, when invoke registerUser, then return EmailEmpty response`() =
        runTest {
            val user = testUser.copy(email = "")
            val expectedResp = RegisterResponse.EmailEmpty().asError

            val errorResp = registerUC(user.firstName, user.lastName, user.email, pw1, pw2)
            assert(errorResp is Resource.Error)
            assertEquals(
                expectedResp.message!!.asString(::getString),
                errorResp.message!!.asString(::getString)
            )
        }

    @Test
    fun `when password is not the same, then returns error`() = runTest {
        val expectedError = RegisterResponse.PWNotSame().asError
        val errorResp =
            registerUC(testUser.firstName, testUser.lastName, testUser.email, "pw1", "pw2")

        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message!!.asString(::getString),
            errorResp.message!!.asString(::getString)
        )
    }

    @Test
    fun `when UserRepository returns error while registering user, then returns error`() = runTest {
        val expectedError = "DATABASE_ERROR".asResourceError<Unit>()
        coEvery { mockUserRepo.registerUser(testUser.email, pw1) } returns expectedError

        val errorResp = registerUC(testUser.firstName, testUser.lastName, testUser.email, pw1, pw2)

        assert(errorResp is Resource.Error)
        assertEquals(expectedError.message, errorResp.message)
        coVerify { mockUserRepo.registerUser(testUser.email, pw1) }
    }

    @Test
    fun `when UserRepository returns error while saving user, then returns error`() = runTest {
        val expectedError = "DATABASE_ERROR".asResourceError<User>()
        coEvery { mockUserRepo.saveUser(any()) } returns expectedError

        val successResp = registerUC(testUser.firstName, testUser.lastName, testUser.email, pw1, pw2)

        assert(successResp is Resource.Error)
        assertEquals(expectedError.message, successResp.message)
        coVerifyOrder {
            mockUserRepo.registerUser(testUser.email, pw1)
            mockUserRepo.saveUser(any())
        }
    }
}
