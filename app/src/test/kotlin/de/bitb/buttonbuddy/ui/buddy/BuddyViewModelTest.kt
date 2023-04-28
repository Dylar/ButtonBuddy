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
import de.bitb.buttonbuddy.usecase.BuddyUseCases
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
    private lateinit var buddyUC: BuddyUseCases

    private lateinit var testBuddy: Buddy

    private val uuid = "uuid1"

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
        buddyUC = mockk()
        viewModel = BuddyViewModel(messageUC, settingsRepo, msgRepo, buddyRepo, buddyUC)
        viewModel.showSnackbar = mockk()
        justRun { viewModel.showSnackbar(any()) }

        testBuddy = buildBuddy()
    }

    @Test
    fun `on initLiveState load buddy and messages for uuid`() = runTest {
        viewModel.initLiveState(uuid)

        verify { buddyRepo.getLiveBuddy(uuid) }
        verify { msgRepo.getLiveMessages(uuid) }
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
