package de.bitb.buttonbuddy.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator

data class PreferenceItem(val title: String, val subtitle: String)

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {
    companion object {
        const val APPBAR_TAG = "SettingAppbar"
    }

    override val viewModel: SettingsViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        val settings by viewModel.settingsRepo.getLiveSettings().observeAsState(null)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Surface(elevation = 4.dp) {
                    TopAppBar(
                        title = { Text(text = getString(R.string.settings_title)) },
                        contentColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp,
//                        navigationIcon = {
//                            IconButton(
//                                onClick = onNavigateBack,
//                                content = {
//                                    Icon(
//                                        imageVector = Icons.Default.ArrowBack,
//                                        contentDescription = null,
//                                    )
//                                },
//                            )
//                        },
                        modifier = Modifier.statusBarsPadding(),
                    )
                }
            },
        ) { innerPadding ->
            when {
                settings != null -> SettingPage(innerPadding, settings!!)
                else -> LoadingIndicator()
            }
        }
    }

    @Composable
    fun SettingPage(padding: PaddingValues, settings: Settings) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            PreferenceSwitch(
                PreferenceItem("DarkMode", "Ist der DarkMode aktiviert?"),
                checked = settings.isDarkMode,
                onChange = {
                    viewModel.saveSettings(settings.copy(isDarkMode = it))
                },
            )
        }
    }

}
