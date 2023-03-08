package de.bitb.buttonbuddy.ui.buddies

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.ui.buddy.BuddyFragment
import launchFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
class BuddiesFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

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
    fun click_profile_button() {
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

}
