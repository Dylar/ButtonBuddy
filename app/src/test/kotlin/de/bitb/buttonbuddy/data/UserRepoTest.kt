package de.bitb.buttonbuddy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.bitb.buttonbuddy.core.getMessageString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRemoteService: RemoteService
    private lateinit var mockLocalDBMock: LocalDatabase
    private lateinit var userRepo: UserRepository

    private lateinit var testUser: User

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRemoteService = mockk()
        mockLocalDBMock = mockk()
        userRepo = UserRepositoryImpl(mockRemoteService, mockLocalDBMock)

        testUser = buildUser()
    }

    @Test
    fun `is user logged in error, should return error`() = runTest {
        val expectedError = "Login user error".asResourceError<Boolean>()
        coEvery { mockRemoteService.isUserLoggedIn() } returns expectedError

        val errorResp = userRepo.isUserLoggedIn()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString()
        )
    }

    @Test
    fun `user not logged in, should return success`() = runTest {
        coEvery { mockRemoteService.isUserLoggedIn() } returns Resource.Success(false)

        val successResp = userRepo.isUserLoggedIn()
        assert(successResp is Resource.Success)
        assertEquals(false, successResp.data)
    }

    @Test
    fun `user logged in, should return success`() = runTest {
        coEvery { mockRemoteService.isUserLoggedIn() } returns Resource.Success(true)

        val successResp = userRepo.isUserLoggedIn()
        assert(successResp is Resource.Success)
        assertEquals(true, successResp.data)
    }

    @Test
    fun `get user failed, should return error`() = runTest {
        val expectedError = Exception("Get user error")
        coEvery { mockLocalDBMock.getUser() } throws expectedError

        val errorResp = userRepo.getLocalUser()
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.getMessageString()
        )
    }

    @Test
    fun `get user, should return success`() = runTest {
        coEvery { mockLocalDBMock.getUser() } returns testUser

        val successResp = userRepo.getLocalUser()
        assert(successResp is Resource.Success)
        assert(successResp.hasData)
        assertEquals(testUser, successResp.data)
    }

    @Test
    fun `register user failed, should return error`() = runTest {
        val expectedError = "Register user error".asResourceError<Unit>()
        coEvery { mockRemoteService.registerUser(testUser.email, "pw") } returns expectedError

        val errorResp = userRepo.registerUser(testUser.email, "pw")
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString()
        )
    }

    @Test
    fun `register user, should return success`() = runTest {
        coEvery { mockRemoteService.registerUser(testUser.email, "pw") } returns Resource.Success()

        val successResp = userRepo.registerUser(testUser.email, "pw")
        assert(successResp is Resource.Success)
    }

    @Test
    fun `login user failed, should return error`() = runTest {
        val expectedError = "Login user error".asResourceError<Boolean>()
        coEvery { mockRemoteService.loginUser(testUser.email, "pw") } returns expectedError

        val errorResp = userRepo.loginUser(testUser.email, "pw")
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `login user, loadUser called`() = runTest {
        val userRepo = spyk(userRepo)
        coEvery { mockRemoteService.loginUser(testUser.email, "pw") } returns Resource.Success(true)
        coEvery { userRepo.loadUser(testUser.email) } returns Resource.Success()

        val successResp = userRepo.loginUser(testUser.email, "pw")
        assert(successResp is Resource.Success)
        coVerify { userRepo.loadUser(testUser.email) }
    }

    @Test
    fun `load user, get user failed, should return error`() = runTest {
        val expectedError = "Get user error".asResourceError<User?>()
        coEvery { mockRemoteService.getUser(testUser.email) } returns expectedError

        val errorResp = userRepo.loadUser(testUser.email)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `load user, no user found, should return success with null user`() = runTest {
        coEvery { mockRemoteService.getUser(testUser.email) } returns Resource.Success()

        val successResp = userRepo.loadUser(testUser.email)
        assert(successResp is Resource.Success)
        assertFalse(successResp.hasData)
    }

    @Test
    fun `load user, save user failed, should return error`() = runTest {
        val userRepo = spyk(userRepo)
        val expectedError = "Save user error".asResourceError<User>()
        coEvery { mockRemoteService.getUser(testUser.email) } returns Resource.Success(testUser)
        coEvery { userRepo.saveUser(testUser) } returns expectedError

        val errorResp = userRepo.loadUser(testUser.email)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `load user, should return success with user`() = runTest {
        val userRepo = spyk(userRepo)
        coEvery { mockRemoteService.getUser(testUser.email) } returns Resource.Success(testUser)
        coEvery { userRepo.saveUser(testUser) } returns Resource.Success(testUser)

        val successResp = userRepo.loadUser(testUser.email)
        assert(successResp is Resource.Success)
        assertTrue(successResp.hasData)
    }

    @Test
    fun `save user, get token failed, should return error`() = runTest {
        val expectedError = Exception("get Token failed")
        coEvery { mockLocalDBMock.getToken() } throws expectedError

        val errorResp = userRepo.saveUser(testUser)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `save user, insert user failed, should return error`() = runTest {
        val expectedError = Exception("insert user failed")
        coEvery { mockLocalDBMock.getToken() } returns "tokenUUID"
        coEvery { mockLocalDBMock.insert(any<User>()) } throws expectedError

        val errorResp = userRepo.saveUser(testUser)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `remote save user failed, should return error`() = runTest {
        val expectedError = "Remote save user error".asResourceError<Unit>()
        coEvery { mockLocalDBMock.getToken() } returns "tokenUUID"
        coJustRun { mockLocalDBMock.insert(any<User>()) }
        coEvery { mockRemoteService.saveUser(any()) } returns expectedError

        val errorResp = userRepo.saveUser(testUser)
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `save user succeeded, should return success`() = runTest {
        val user = testUser.copy(uuid = "")
        val newToken = "tokenUUID"
        assertNotEquals(user.token, newToken)

        coEvery { mockLocalDBMock.getToken() } returns newToken
        coJustRun { mockLocalDBMock.insert(any<User>()) }

        val successResp = userRepo.saveUser(user)
        assert(successResp is Resource.Success)
        assertTrue(successResp.hasData)
        assertEquals(successResp.data!!.token, newToken)
        coVerify(exactly = 0) { mockRemoteService.saveUser(any()) }
    }

    @Test
    fun `update token, set token failed, should return error`() = runTest {
        val expectedError = Exception("set token error")
        coEvery { mockLocalDBMock.setToken(any()) } throws expectedError

        val errorResp = userRepo.updateToken("new Token")
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.message,
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `update token, save user failed, should return error`() = runTest {
        val expectedError = "Save user error".asResourceError<Unit>()
        justRun { mockLocalDBMock.setToken(any()) }
        coEvery { mockLocalDBMock.getUser() } returns testUser
        coEvery { mockRemoteService.saveUser(any()) } returns expectedError

        val errorResp = userRepo.updateToken("new Token")
        assert(errorResp is Resource.Error)
        assertEquals(
            expectedError.getMessageString(),
            errorResp.getMessageString(),
        )
    }

    @Test
    fun `update token succeeded, should return error`() = runTest {
        justRun { mockLocalDBMock.setToken(any()) }
        coEvery { mockLocalDBMock.getUser() } returns testUser
        coEvery { mockRemoteService.saveUser(any()) } returns Resource.Success()

        val successResp = userRepo.updateToken("new Token")
        assert(successResp is Resource.Success)
    }
}
