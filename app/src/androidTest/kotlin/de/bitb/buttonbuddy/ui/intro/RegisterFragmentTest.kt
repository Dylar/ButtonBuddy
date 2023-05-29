package de.bitb.buttonbuddy.ui.intro

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.ui.buddies.BuddiesFragment
import de.bitb.buttonbuddy.usecase.user.RegisterResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class RegisterFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun RegisterResponse.asString(): String = message.asString(::getString)

    @Test
    fun render_registerFragment() = runTest {
        composeRule.apply {
            navigateTo(TestNavigation.Register)
            waitForIdle()

            onNodeWithTag(RegisterFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.register_title)))
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithTag(RegisterFragment.EMAIL_TAG)
                .assertIsDisplayed()
//                .assertTextEquals(getString(R.string.email))
//                .onChildren() TODO why not? :/
//                .assertAny(hasText(getString(R.string.email)))
            onNodeWithTag(RegisterFragment.APPBAR_TAG)
                .assertIsDisplayed()
//                .onChildren()
//                .assertAny(hasText(getString(R.string.pw1_label)))
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun test_register_errors() = runTest {
        composeRule.apply {
            fun checkError(resp: RegisterResponse) {
                onNodeWithTag(RegisterFragment.ERROR_TAG)
                    .onChildren()
                    .assertAny(hasText(resp.asString()))
            }

            fun enterPassword(pw: String) {
                onNodeWithTag(RegisterFragment.PW1_TAG).apply {
                    performTextClearance()
                    performTextInput(pw)
                }
                onNodeWithTag(RegisterFragment.PW2_TAG).apply {
                    performTextClearance()
                    performTextInput(pw)
                }
            }
            remoteService.mockWholeService(buildUser(), isLoggedIn = false)

            navigateTo(TestNavigation.Register)
            waitForIdle()

            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.FirstNameEmpty)

            onNodeWithTag(RegisterFragment.FIRST_NAME_TAG).performTextInput("FirstName")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.LastNameEmpty)

            onNodeWithTag(RegisterFragment.LAST_NAME_TAG).performTextInput("LastName")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.EmailError.EmailEmpty)

            onNodeWithTag(RegisterFragment.EMAIL_TAG).performTextInput("email@gmx.de")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWEmpty)

            onNodeWithTag(RegisterFragment.PW1_TAG).performTextInput("PW1")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWEmpty)

            onNodeWithTag(RegisterFragment.PW2_TAG).performTextInput("PW2")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWNotSame)

            enterPassword("abc")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWLengthTooShort)

            enterPassword("abcabcabcabc")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWMissingUppercase)

            enterPassword("ABCABCABCABC")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWMissingLowercase)

            enterPassword("ABCABCABCabc")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWMissingDigit)

            enterPassword("ABCABCABCabc1")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()
            checkError(RegisterResponse.PWError.PWMissingSpecialCharacter)

            enterPassword("ABCABCABCabc1!")
            onNodeWithTag(RegisterFragment.REGISTER_BUTTON_TAG).performClick()
            waitForIdle()

            onNodeWithTag(BuddiesFragment.APPBAR_TAG)
                .assertIsDisplayed()
        }
    }
}
