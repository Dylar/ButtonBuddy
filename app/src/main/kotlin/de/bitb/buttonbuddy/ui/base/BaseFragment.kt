package de.bitb.buttonbuddy.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.styles.ButtonBuddyAppTheme
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    abstract val viewModel: T

    val navController by lazy { NavHostFragment.findNavController(this) }
    lateinit var scaffoldState: ScaffoldState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigate = { navController.navigate(it) }
        viewModel.navigateBack = { id ->
            navController.apply {
                id?.let { popBackStack(id, false) } ?: popBackStack()
            }
        }
        viewModel.showSnackbar = ::showSnackBar
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            scaffoldState = rememberScaffoldState()
            val settings by viewModel.settingsRepo.getLiveSettings().observeAsState()
            ButtonBuddyAppTheme(
                useDarkTheme = settings?.isDarkMode ?: isSystemInDarkTheme()
            ) { ScreenContent() }
        }
    }

    @Composable
    abstract fun ScreenContent()

    private fun showSnackBar(string: ResString) {
        lifecycleScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = string.asString(resources::getString),
//                    actionLabel = "Do something"
            )
        }
    }
}