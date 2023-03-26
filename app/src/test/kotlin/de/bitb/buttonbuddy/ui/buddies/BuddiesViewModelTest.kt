package de.bitb.buttonbuddy.ui.buddies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.ui.base.composable.asResString
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BuddiesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: BuddiesViewModel
    private lateinit var buddyUC: BuddyUseCases
    private lateinit var messageUC: MessageUseCases

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val infoRepo = mockk<UserRepository>()
        val userLiveData = mockk<LiveData<User>>()
        every { infoRepo.getLiveUser() }.returns(userLiveData)

        val buddyRepo = mockk<BuddyRepository>()
        val buddiesLiveData = mockk<LiveData<List<Buddy>>>()
        every { buddyRepo.getLiveBuddies() }.returns(buddiesLiveData)

        buddyUC = mockk()
        messageUC = mockk()
        viewModel = BuddiesViewModel(messageUC, buddyUC, buddyRepo)
        viewModel.showSnackbar = mockk()
        justRun { viewModel.showSnackbar.invoke(any()) }
    }

    @Test
    fun `refreshData sets isRefreshing to true then false - show success message`() = runTest {
        // Given
        coEvery { buddyUC.loadBuddiesUC() } coAnswers {
            delay(1L)
            Resource.Success(Unit)
        }

        // When
        viewModel.refreshData()

        // Then
        assertEquals(true, viewModel.isRefreshing.value)
        advanceTimeBy(2L)
        assertEquals(false, viewModel.isRefreshing.value)
        verify { viewModel.showSnackbar("Buddys geladen".asResString()) }
    }

    @Test
    fun `refreshData shows error if loading buddies fails`() = runTest {
        // Given
        val errorMessage = "Error message".asResString()
        coEvery { buddyUC.loadBuddiesUC() } returns Resource.Error(errorMessage)

        // When
        viewModel.refreshData()
        advanceTimeBy(1L)

        // Then
        verify { viewModel.showSnackbar(errorMessage) }
    }

    @Test
    fun `sendMessage shows error if sending message fails`() = runTest {
        // Given
        val buddy = buildBuddy()
        val errorMessage = "Error message".asResString()
        coEvery { messageUC.sendMessageUC(buddy) } returns Resource.Error(errorMessage)

        // When
        viewModel.sendMessage(buddy)
        advanceTimeBy(1L)

        // Then
        verify { viewModel.showSnackbar(errorMessage) }
    }

    @Test
    fun `sendMessage shows success if sending message succeeds`() = runTest {
        // Given
        val buddy = buildBuddy()
        coEvery { messageUC.sendMessageUC(buddy) } returns Resource.Success(Unit)

        // When
        viewModel.sendMessage(buddy)
        advanceTimeBy(1L)

        // Then
        verify { viewModel.showSnackbar("Nachricht gesendet".asResString()) }
    }
}
