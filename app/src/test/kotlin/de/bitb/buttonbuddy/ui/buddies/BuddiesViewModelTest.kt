package de.bitb.buttonbuddy.ui.buddies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.asResString
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.UserUseCases
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
    private lateinit var userUC: UserUseCases
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

        val settingsRepo = mockk<SettingsRepository>()
        val settingLiveData = mockk<LiveData<Settings>>()
        every { settingsRepo.getLiveSettings() }.returns(settingLiveData)

        messageUC = mockk()
        viewModel = BuddiesViewModel(messageUC, settingsRepo, userUC, mockk(), mockk())
        viewModel.showSnackbar = mockk()
        justRun { viewModel.showSnackbar.invoke(any()) }
    }

    @Test
    fun `refreshData sets isRefreshing to true then false - show success message`() = runTest {
        // Given
        coEvery { userUC.loadDataUC() } coAnswers {
            delay(1L)
            Resource.Success()
        }

        // When
        viewModel.refreshData()

        // Then
        assertEquals(true, viewModel.isRefreshing.value)
        advanceTimeBy(2L)
        assertEquals(false, viewModel.isRefreshing.value)
        verify {
            viewModel.showSnackbar(
                match {
                    it.asString(::getString) ==
                            R.string.buddies_loaded.asResString()
                                .asString(::getString)
                },
            )
        }
    }

    @Test
    fun `refreshData shows error if loading buddies fails`() = runTest {
        // Given
        val errorMessage = "Error message".asResString()
        coEvery {userUC.loadDataUC() } returns Resource.Error(errorMessage)

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
        viewModel.sendMessageToBuddy(buddy)
        advanceTimeBy(1L)

        // Then
        verify { viewModel.showSnackbar(errorMessage) }
    }

    @Test
    fun `sendMessage shows success if sending message succeeds`() = runTest {
        // Given
        val buddy = buildBuddy()
        coEvery { messageUC.sendMessageUC(buddy) } returns Resource.Success()

        // When
        viewModel.sendMessageToBuddy(buddy)
        advanceTimeBy(1L)

        // Then
        verify {
            viewModel.showSnackbar(
                match {
                    it.asString(::getString) == ResString.ResourceString(
                        R.string.message_sent_toast,
                        arrayOf(buddy.fullName),
                    ).asString(::getString)
                },
            )
        }
    }
}
