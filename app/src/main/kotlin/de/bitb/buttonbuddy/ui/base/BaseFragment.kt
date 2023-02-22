package de.bitb.buttonbuddy.ui.base

import android.os.Bundle
import android.view.View
import androidx.compose.material.ScaffoldState
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import de.bitb.buttonbuddy.ui.BACK_ID
import kotlinx.coroutines.launch

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    abstract val viewModel: T

    lateinit var scaffoldState: ScaffoldState

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigate = { id, popId ->
            NavHostFragment.findNavController(this).apply {
                if (id == BACK_ID) {
                    popBackStack()
                } else {
                    if (popId != null) {
                        popBackStack(popId, false)
                    }
                    navigate(id)
                }
            }
        }
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