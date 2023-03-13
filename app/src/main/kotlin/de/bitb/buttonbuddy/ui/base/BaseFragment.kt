package de.bitb.buttonbuddy.ui.base

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import de.bitb.buttonbuddy.ui.base.composable.ResString
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    abstract val viewModel: T

    val navController by lazy { NavHostFragment.findNavController(this) }
    lateinit var scaffoldState: ScaffoldState

    @Composable // TODO make depending on settings
    fun isDarkMode() = isSystemInDarkTheme()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigate = { id, popId ->
            navController.apply {
                if (id == BACK_ID) {
                    popBackStack()
                } else {
                    if (popId != null) { // TODO thats not working
                        popBackStack(popId, false)
                    }
                    navigate(id)
                }
            }
        }
        viewModel.showSnackBar = ::showSnackBar
    }

    fun showSnackBar(string: ResString) {
        lifecycleScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = string.asString(resources::getString),
//                    actionLabel = "Do something"
            )
        }
    }
}