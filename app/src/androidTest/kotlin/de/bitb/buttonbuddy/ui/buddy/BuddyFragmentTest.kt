package de.bitb.buttonbuddy.ui.buddy

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.core.misc.calculateMilliseconds
import de.bitb.buttonbuddy.core.misc.formatDuration
import de.bitb.buttonbuddy.core.misc.getHours
import de.bitb.buttonbuddy.core.misc.getMins
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildMessage
import de.bitb.buttonbuddy.shared.buildUser
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

private fun String.pad() = this.padStart(2, '0')

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class BuddyFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var remoteService: RemoteService

    @Inject
    lateinit var localDatabase: LocalDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun render_buddyFragment() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val user = buildUser(mutableListOf(buddy.uuid))
            remoteService.mockWholeService(user, buddies = listOf(buddy))

            launchActivity(TestNavigation.BuddyDetail(user, buddy = buddy))
            waitForIdle()
            onNodeWithTag(BuddyFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.buddy_title, ": ${buddy.fullName}")))
            onNodeWithTag(BuddyFragment.SEND_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithText(getString(R.string.buddy_no_messages)).assertIsDisplayed()
            onNodeWithTag(BuddyFragment.SEND_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithTag(BuddyFragment.COOLDOWN_BUTTON_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun renderMessages() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val user = buildUser(mutableListOf(buddy.uuid))
            val message1 = buildMessage(uuid = "msgUuid1", date = Date(200000000))
            val message2 = buildMessage(uuid = "msgUuid2", "uuid2", "uuid1", Date(100000000))
            val messages = listOf(message1, message2)
            remoteService.mockWholeService(user, buddies = listOf(buddy))
            localDatabase.mockLocalDatabase(messages)

            launchActivity(TestNavigation.BuddyDetail(user, buddy = buddy))
            waitForIdle()

            onNodeWithTag(BuddyFragment.LIST_TAG)
                .apply {
                    onChildren().assertCountEquals(messages.size)
                    onChildAt(0)
                        .onChildren()
                        .assertAny(hasText(messages[1].formatDate))
                    onChildAt(1)
                        .onChildren()
                        .assertAny(hasText(messages[0].formatDate))
                }
        }
    }

    @Test
    fun tap_cooldownButton_opens_timePicker_selectTime_updateCooldown() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val user = buildUser(mutableListOf(buddy.uuid))
            remoteService.mockWholeService(user, buddies = listOf(buddy))

            launchActivity(TestNavigation.BuddyDetail(user, buddy = buddy))
            waitForIdle()

            val oldHour = getHours(buddy.cooldown)
            val oldMin = getMins(buddy.cooldown)
            val oldCooldown = formatDuration(calculateMilliseconds(oldHour, oldMin))
            val newHour = 10
            val newMin = 5
            val newCooldown = calculateMilliseconds(newHour, newMin)
            onNodeWithTag(BuddyFragment.COOLDOWN_BUTTON_TAG).assertTextEquals(oldCooldown)
            onNodeWithTag(TimePickerTags.TIME_PICKER_DIALOG).assertDoesNotExist()
            onNodeWithTag(BuddyFragment.COOLDOWN_BUTTON_TAG).performClick()
            waitForIdle()
            onNodeWithTag(TimePickerTags.TIME_PICKER_DIALOG).assertIsDisplayed()
            onNodeWithTag(TimePickerTags.TIME_PICKER_CANCEL).assertIsDisplayed()
            onNodeWithTag(TimePickerTags.TIME_PICKER_OK).assertIsDisplayed()
            for (i in 0..23) {
                onNodeWithTag(TimePickerTags.timePickerMark(i.toString())).assertIsDisplayed()
            }

            onNodeWithTag(TimePickerTags.TIME_PICKER_HOUR).assertTextEquals("$oldHour".pad())
            onNodeWithTag(TimePickerTags.TIME_PICKER_MIN).assertTextEquals("$oldMin".pad())
            onNodeWithTag(TimePickerTags.timePickerMark("$newHour".pad())).performClick()
            onNodeWithTag(TimePickerTags.TIME_PICKER_HOUR).assertTextEquals("$newHour".pad())
            onNodeWithTag(TimePickerTags.TIME_PICKER_MIN).assertTextEquals("$oldMin".pad())
            onNodeWithTag(TimePickerTags.TIME_PICKER_MIN).performClick()
            onNodeWithTag(TimePickerTags.timePickerMark("$newMin").pad()).performClick()
            onNodeWithTag(TimePickerTags.TIME_PICKER_HOUR).assertTextEquals("$newHour".pad())
            onNodeWithTag(TimePickerTags.TIME_PICKER_MIN).assertTextEquals("$newMin".pad())

            onNodeWithTag(TimePickerTags.TIME_PICKER_OK).performClick()

            onNodeWithTag(BuddyFragment.COOLDOWN_BUTTON_TAG).assertTextEquals(oldCooldown)

            coVerify { remoteService.updateCooldown(any(), any(), newCooldown) }
        }
    }

}
