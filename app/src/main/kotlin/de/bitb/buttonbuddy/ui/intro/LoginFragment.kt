package de.bitb.buttonbuddy.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
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
import de.bitb.buttonbuddy.ui.base.naviToRegister
import de.bitb.buttonbuddy.ui.base.styles.BaseColors
import de.bitb.buttonbuddy.ui.base.styles.createComposeView

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {
    companion object {
        const val APPBAR_TAG = "LoginAppbar"
        const val USER_NAME_TAG = "LoginUserName"
        const val PW_TAG = "LoginPW"
        const val REGISTER_BUTTON_TAG = "LoginRegisterButton"
        const val LOGIN_BUTTON_TAG = "LoginButton"
        const val ERROR_TAG = "LoginError"
    }

    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.login_title)) })
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(all = 32.dp)
                        .testTag(LOGIN_BUTTON_TAG),
                    onClick = { viewModel.login() }
                ) { Icon(Icons.Default.ArrowForward, contentDescription = "Login") }
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
                        .testTag(USER_NAME_TAG),
                    value = viewModel.userName,
                    onValueChange = { viewModel.userName = it },
                    label = { Text(getString(R.string.user_name)) },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW_TAG),
                    value = viewModel.pw,
                    onValueChange = { viewModel.pw = it },
                    label = { Text(getString(R.string.pw1_label)) }
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
                Text(
                    modifier = Modifier
                        .clickable(onClick = ::naviToRegister)
                        .testTag(REGISTER_BUTTON_TAG),
                    text = getString(R.string.login_register_account),
                )
            }
        }
    }
}