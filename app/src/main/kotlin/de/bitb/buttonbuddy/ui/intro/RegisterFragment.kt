package de.bitb.buttonbuddy.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.styles.BaseColors
import de.bitb.buttonbuddy.ui.base.styles.createComposeView

@AndroidEntryPoint
class RegisterFragment : BaseFragment<RegisterViewModel>() {
    companion object {
        const val APPBAR_TAG = "RegisterAppbar"
        const val FIRST_NAME_TAG = "RegisterFirstName"
        const val LAST_NAME_TAG = "RegisterLastName"
        const val USER_NAME_TAG = "RegisterUserName"
        const val PW1_TAG = "RegisterPW1"
        const val PW2_TAG = "RegisterPW2"
        const val REGISTER_BUTTON_TAG = "RegisterButton"
        const val ERROR_TAG = "RegisterError"
    }

    override val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.register_title)) })
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(all = 32.dp)
                        .testTag(REGISTER_BUTTON_TAG),
                    onClick = { viewModel.register() }
                ) { Icon(Icons.Default.ArrowForward, contentDescription = "Register") }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .testTag(FIRST_NAME_TAG),
                    value = viewModel.firstName,
                    onValueChange = { viewModel.firstName = it },
                    label = { Text(getString(R.string.first_name)) },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(LAST_NAME_TAG),
                    value = viewModel.lastName,
                    onValueChange = { viewModel.lastName = it },
                    label = { Text(getString(R.string.last_name)) }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(USER_NAME_TAG),
                    value = viewModel.userName,
                    onValueChange = { viewModel.userName = it },
                    label = { Text(getString(R.string.user_name)) }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW1_TAG),
                    value = viewModel.pw1,
                    onValueChange = { viewModel.pw1 = it },
                    label = { Text(getString(R.string.pw1_label)) }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW2_TAG),
                    value = viewModel.pw2,
                    onValueChange = { viewModel.pw2 = it },
                    label = { Text(getString(R.string.pw2_label)) }
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                viewModel.error?.let {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag(ERROR_TAG),
                        contentAlignment = Alignment.TopCenter,
                    ) { Text(it.asString(), color = BaseColors.FireRed) }
                }
            }
        }
    }
}