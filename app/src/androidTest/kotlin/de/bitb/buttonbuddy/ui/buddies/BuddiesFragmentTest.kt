package de.bitb.buttonbuddy.ui.buddies

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.buildBuddy
import de.bitb.buttonbuddy.buildInfo
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.ui.buddy.BuddyFragment
import de.bitb.buttonbuddy.ui.scan.ScanFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import launchFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class BuddiesFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var infoRepository: InfoRepository

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun render_buddiesFragment() {
        composeRule.apply {
            launchFragment<BuddiesFragment>()
            waitForIdle()
            onNodeWithTag(BuddiesFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.buddies_title)))
            onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithText(getString(R.string.no_buddies))
                .assertIsDisplayed()
            onNodeWithTag(BuddiesFragment.SCAN_BUTTON_TAG)
                .assertIsDisplayed()
//        onNodeWithTag(BuddiesFragment.REFRESH_INDICATOR_TAG)
//            .assertExists()
//            .assertIsNotDisplayed()
        }
    }

    @Test
    fun render_buddiesList() = runTest {
        composeRule.apply {
            val info = buildInfo()
            val buddy = buildBuddy()
            val buddies =
                listOf(
                    buddy.copy(uuid = "uuid1", firstName = "first1"),
                    buddy.copy(uuid = "uuid2", firstName = "first2"),
                    buddy.copy(uuid = "uuid3", firstName = "first3")
                )
            infoRepository.saveInfo(info)
            remoteService.mockRemoteService(info, buddies)

            launchFragment<BuddiesFragment>()
            waitForIdle()
            onNodeWithText(getString(R.string.no_buddies))
                .assertDoesNotExist()
            onAllNodesWithTag(BuddiesFragment.LIST_TAG)
                .onFirst() // but why multiple?
                .apply {
                    onChildren().assertCountEquals(buddies.size)
                    onChildAt(0).assert(hasText(buddies[0].fullName))
                    onChildAt(1).assert(hasText(buddies[1].fullName))
                    onChildAt(2).assert(hasText(buddies[2].fullName))
                }
        }
    }

    @Test
    fun clickSendButton_success() = runTest {
        composeRule.apply {
            val info = buildInfo()
            val buddy = buildBuddy()
            infoRepository.saveInfo(info)
            remoteService.mockRemoteService(info, listOf(buddy))

            launchFragment<BuddiesFragment>()
            waitForIdle()

            val snackMsg = String.format(getString(R.string.message_sent_toast), buddy.fullName)
            onNodeWithText(snackMsg)
                .assertDoesNotExist()
            onAllNodesWithTag(BuddiesFragment.buddySendButtonTag(buddy))
                .onFirst() // but why multiple?
                .performClick()
            onNodeWithText(snackMsg)
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickSendButton_error() = runTest {
        composeRule.apply {
            val info = buildInfo()
            val buddy = buildBuddy()
            val error = "ERROR"
            infoRepository.saveInfo(info)
            remoteService.mockRemoteService(info, listOf(buddy), sendMessageError = error)

            launchFragment<BuddiesFragment>()
            waitForIdle()

            onNodeWithText(error)
                .assertDoesNotExist()
            onAllNodesWithTag(BuddiesFragment.buddySendButtonTag(buddy))
                .onFirst() // but why multiple?
                .performClick()
            onNodeWithText(error)
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickProfileButton_ShowNoUUIDSnackbar() {
        composeRule.apply {
            launchFragment<BuddiesFragment>()
            waitForIdle()

            onNodeWithText(getString(R.string.no_uuid))
                .assertDoesNotExist()
            onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
                .performClick()
            onNodeWithText(getString(R.string.no_uuid))
                .assertIsDisplayed()
            onNodeWithTag(BuddyFragment.APPBAR_TAG)
                .assertDoesNotExist()
        }
    }

    @Test
    fun clickProfileButton_navigateToProfile() = runTest {
        composeRule.apply {
            val info = buildInfo()
            infoRepository.saveInfo(info)
            remoteService.mockRemoteService(info)

            launchFragment<BuddiesFragment>()
            waitForIdle()

            onNodeWithText(getString(R.string.no_uuid))
                .assertDoesNotExist()
            onAllNodesWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
                .onFirst() // but why multiple?
                .performClick()
            onNodeWithText(getString(R.string.no_uuid))
                .assertDoesNotExist()

            waitForIdle()
            onNodeWithTag(BuddyFragment.APPBAR_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickScanButton_navigateToScan() = runTest {
        composeRule.apply {
            val info = buildInfo()
            infoRepository.saveInfo(info)
            remoteService.mockRemoteService(info)

            launchFragment<BuddiesFragment>()
            waitForIdle()

            onAllNodesWithTag(BuddiesFragment.SCAN_BUTTON_TAG)
                .onFirst() // but why multiple?
                .performClick()

            waitForIdle()
            onNodeWithTag(ScanFragment.APPBAR_TAG)
                .assertIsDisplayed()
        }
    }

}
