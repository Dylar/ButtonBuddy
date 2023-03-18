package de.bitb.buttonbuddy.core

import android.content.ComponentName
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.buddies.BuddiesFragment
import de.bitb.buttonbuddy.ui.intro.LoginFragment

sealed class TestNavigation {
    object Splash : TestNavigation()
    object Login : TestNavigation()
    data class Buddies(val info: Info) : TestNavigation()
    data class BuddyDetail(val info: Info, val buddy: Buddy) : TestNavigation()
    object Profile : TestNavigation()
    object Scan : TestNavigation()
    object Settings : TestNavigation()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.launchActivity(
    naviTo: TestNavigation,
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        R.style.Theme_ButtonBuddy
    )

    ActivityScenario.launch<TestActivity>(startActivityIntent)
    waitForIdle()

    when (naviTo) {
        is TestNavigation.BuddyDetail -> doLogin(naviTo.info).also { tapBuddy(naviTo.buddy) }
        TestNavigation.Login -> doNothing()
        is TestNavigation.Buddies -> doLogin(naviTo.info)
        TestNavigation.Profile -> TODO()
        TestNavigation.Scan -> TODO()
        TestNavigation.Settings -> TODO()
        TestNavigation.Splash -> TODO()
    }
}

fun doNothing() {}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.tapBuddy(buddy: Buddy) {
    onNodeWithTag(BuddiesFragment.LIST_TAG)
        .onChildren()
        .filterToOne(hasText(buddy.fullName))
        .performClick()
    waitForIdle()
}


fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.doLogin(info: Info) {
    onNodeWithTag(LoginFragment.FIRST_NAME_TAG).performTextInput(info.firstName)
    onNodeWithTag(LoginFragment.LAST_NAME_TAG).performTextInput(info.lastName)
    onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG).performClick()
    waitForIdle()
}
