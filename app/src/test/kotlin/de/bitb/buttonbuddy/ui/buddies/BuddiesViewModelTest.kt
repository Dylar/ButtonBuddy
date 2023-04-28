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

    private lateinit var testBuddy: Buddy

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val buddyRepo = mockk<BuddyRepository>()
        val buddiesLiveData = mockk<LiveData<List<Buddy>>>()
        every { buddyRepo.getLiveBuddies() }.returns(buddiesLiveData)

        val settingsRepo = mockk<SettingsRepository>()
        val settingLiveData = mockk<LiveData<Settings>>()
        every { settingsRepo.getLiveSettings() }.returns(settingLiveData)

        messageUC = mockk()
        userUC = mockk()
        viewModel = BuddiesViewModel(messageUC, settingsRepo, userUC, mockk(), buddyRepo)
        viewModel.showSnackbar = mockk()
        justRun { viewModel.showSnackbar.invoke(any()) }

        testBuddy = buildBuddy()
    }

    @Test
    fun `refreshData sets isRefreshing to true then false - show success message`() = runTest {
        coEvery { userUC.loadDataUC() } coAnswers {
            delay(1L)
            Resource.Success()
        }

        viewModel.refreshData()

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
        val errorMessage = "Error message".asResString()
        coEvery { userUC.loadDataUC() } returns Resource.Error(errorMessage)

        viewModel.refreshData()
        advanceTimeBy(1L)

        verify { viewModel.showSnackbar(errorMessage) }
    }

    @Test
    fun `sendMessage shows error if sending message fails`() = runTest {
        val errorMessage = "Error message".asResString()
        coEvery { messageUC.sendMessageUC(testBuddy) } returns Resource.Error(errorMessage)

        viewModel.sendMessageToBuddy(testBuddy)
        advanceTimeBy(1L)

        verify { viewModel.showSnackbar(errorMessage) }
    }

    @Test
    fun `sendMessage shows success if sending message succeeds`() = runTest {
        coEvery { messageUC.sendMessageUC(testBuddy) } returns Resource.Success()

        viewModel.sendMessageToBuddy(testBuddy)
        advanceTimeBy(1L)

        verify {
            viewModel.showSnackbar(
                match {
                    it.asString(::getString) == ResString.ResourceString(
                        R.string.message_sent_toast,
                        arrayOf(testBuddy.fullName),
                    ).asString(::getString)
                },
            )
        }
    }
}
