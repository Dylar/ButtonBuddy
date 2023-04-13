package de.bitb.buttonbuddy.ui.buddy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.asResString
import de.bitb.buttonbuddy.usecase.MessageUseCases
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class BuddyViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: BuddyViewModel
    private lateinit var buddyRepo: BuddyRepository
    private lateinit var msgRepo: MessageRepository

    private lateinit var messageUC: MessageUseCases

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val userRepo = mockk<UserRepository>()
        val userLiveData = mockk<LiveData<User>>()
        every { userRepo.getLiveUser() }.returns(userLiveData)

        buddyRepo = mockk()
        val buddyLiveData = mockk<LiveData<Buddy>>()
        every { buddyRepo.getLiveBuddy(any()) }.returns(buddyLiveData)

        msgRepo = mockk()
        val msgLiveData = mockk<LiveData<List<Message>>>()
        every { msgRepo.getLiveMessages(any()) }.returns(msgLiveData)

        val settingsRepo = mockk<SettingsRepository>()
        val settingLiveData = mockk<LiveData<Settings>>()
        every { settingsRepo.getLiveSettings() }.returns(settingLiveData)

        messageUC = mockk()
        viewModel = BuddyViewModel(messageUC, buddyRepo, msgRepo, settingsRepo, userRepo)
        viewModel.showSnackbar = mockk()
        justRun { viewModel.showSnackbar(any()) }
    }

    @Test
    fun `on initLiveState load buddy and messages for uuid`() = runTest {
        // Given
        val uuid = "uuid1"

        // When
        viewModel.initLiveState(uuid)

        // Then
        verify { buddyRepo.getLiveBuddy(uuid) }
        verify { msgRepo.getLiveMessages(uuid) }
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
        coEvery { messageUC.sendMessageUC(buddy) } returns Resource.Success(Unit)

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
