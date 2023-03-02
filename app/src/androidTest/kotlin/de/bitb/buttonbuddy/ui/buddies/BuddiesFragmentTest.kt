package de.bitb.buttonbuddy.ui.buddies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.bitb.buttonbuddy.buildBuddy
import de.bitb.buttonbuddy.buildInfo
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.launchFrag
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuddiesFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavController = mockk<NavController>(relaxed = true)

    @Before
    fun setUp() {
        clearAllMocks()
        every { mockNavController.navigate(any<Int>()) } returns Unit
    }

    private fun createModelView(
        infoInput: Info? = null,
        buddyInput: List<Buddy>? = null
    ): BuddiesViewModel {
        val infoObj = infoInput ?: buildInfo()
        val buddyObj = buddyInput ?: listOf(buildBuddy())
        return mockk<BuddiesViewModel>().apply {
            every { buddies } returns MutableLiveData(buddyObj)
            every { info } returns MutableLiveData(infoObj)
            every { refreshData() } answers { nothing }
            every { sendMessage(any()) } answers { nothing }
            every { isRefreshing } returns mutableStateOf(false)
        }
    }

    @Test
    fun buddiesList_isDisplayed() {
        val info = buildInfo()
        val buddy = buildBuddy()
        launchFrag<BuddiesViewModel, BuddiesFragment>(createModelView(info, listOf(buddy)))

        composeTestRule.onNodeWithText(buddy.fullName).assertExists()
    }

}
