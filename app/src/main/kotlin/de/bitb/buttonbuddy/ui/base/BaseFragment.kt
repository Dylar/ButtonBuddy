package de.bitb.buttonbuddy.ui.base

import android.os.Bundle
import android.view.View
import androidx.compose.material.ScaffoldState
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    abstract val viewModel: T

    lateinit var scaffoldState: ScaffoldState
    val navController by lazy { NavHostFragment.findNavController(this) }

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
        viewModel.showSnackbar = ::showSnackBar
    }

    fun showSnackBar(error: String) {
        lifecycleScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = error,
//                    actionLabel = "Do something"
            )
        }
    }
}