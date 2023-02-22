package de.bitb.buttonbuddy.ui.scan

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.composable.LifecycleComp
import de.bitb.buttonbuddy.ui.openAppSettings
import de.bitb.buttonbuddy.ui.permission.CameraPermissionTextProvider
import de.bitb.buttonbuddy.ui.permission.PermissionDialog

import de.bitb.buttonbuddy.ui.styles.createComposeView

@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewModel>() {
    override val viewModel: ScanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView { BuddiesScreen() }

    @Composable
    fun BuddiesScreen() {
        scaffoldState = rememberScaffoldState()

        val dialogQueue = viewModel.visiblePermissionDialogQueue
        val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                viewModel.onPermissionResult(
                    permission = Manifest.permission.CAMERA,
                    isGranted = isGranted
                )
            }
        )

        Scaffold(
            topBar = { TopAppBar(title = { Text("Scan") }) },
        ) { innerPadding ->
            ScannerPreview(innerPadding)
            cameraPermissionResultLauncher.launch(
                Manifest.permission.CAMERA
            )
        }

        dialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.CAMERA -> {
                            CameraPermissionTextProvider()
                        }
                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        permission,
                    ),
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        viewModel.dismissDialog()
//                        multiplePermissionResultLauncher.launch(
//                            arrayOf(permission),
//                        )
                    },
                    onGoToAppSettingsClick = { activity?.openAppSettings() },
                )
            }
    }

    @Composable
    fun ScannerPreview(innerPadding: PaddingValues) {
        var codeScanner: CodeScanner? = null
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                CodeScannerView(context).apply {
                    isMaskVisible = true
                    CodeScanner(context, this).apply {
                        codeScanner = this
                        camera =
                            CodeScanner.CAMERA_BACK
                        formats = CodeScanner.ALL_FORMATS
                        autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
                        scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
                        isAutoFocusEnabled = true // Whether to enable auto focus or not
                        isFlashEnabled = false // Whether to enable flash or not
                        errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                            Log.e(this@ScanFragment.tag, it.toString())
                        }

                        decodeCallback = DecodeCallback { viewModel.onScan(it.text) }
                        setOnClickListener { startPreview() }
                    }
                }
            },
        )
        val error = viewModel.error
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        LaunchedEffect(key1 = error) {
            codeScanner?.startPreview()
            if (error != null) {
//                val snackbarResult =
                scaffoldState.snackbarHostState.showSnackbar(
                    message = error.asString { id, args -> //TODO make anders
                        activity?.resources?.getString(id, *args) ?: ""
                    },
//                    actionLabel = "Do something"
                )
//                when (snackbarResult) {
//                    SnackbarResult.Dismissed -> TODO()
//                    SnackbarResult.ActionPerformed -> TODO()
//                }
            }
        }
        LifecycleComp { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> codeScanner?.startPreview()
                Lifecycle.Event.ON_PAUSE -> codeScanner?.releaseResources()
                else -> {}
            }
        }
    }
}