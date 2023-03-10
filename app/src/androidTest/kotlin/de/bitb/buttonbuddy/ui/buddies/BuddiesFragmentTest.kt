package de.bitb.buttonbuddy.ui.buddies

import android.util.Log
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.buildInfo
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.ui.buddy.BuddyFragment
import de.bitb.buttonbuddy.usecase.InfoUseCases
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(BuddiesFragment.APPBAR_TAG)
            .assertIsDisplayed()
            .assertTextOnChildren(getString(R.string.buddies_title))
        composeRule.onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
            .assertIsDisplayed()

    }

    @Test
    fun clickProfileButton_ShowNoUUIDSnackbar() {
        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertDoesNotExist()
        composeRule.onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
            .performClick()
        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(BuddyFragment.APPBAR_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun clickProfileButton_ShowNoNoUUIDSnackbar_navigateToProfile() = runTest {
        val info = buildInfo()
        infoRepository.saveInfo(info)
        coEvery { remoteService.getInfo(any(), any()) }.returns(Resource.Success(info))
        coEvery { remoteService.loadBuddies(any()) }.returns(Resource.Success(emptyList()))


        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertDoesNotExist()
        composeRule.onAllNodesWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
            .onFirst() // but why are more?
            .performClick()
        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertDoesNotExist()

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(BuddyFragment.APPBAR_TAG)
            .assertIsDisplayed()
    }

}
