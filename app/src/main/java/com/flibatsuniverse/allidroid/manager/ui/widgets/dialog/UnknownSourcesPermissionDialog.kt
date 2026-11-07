package com.flibatsuniverse.allidroid.manager.ui.widgets.dialog

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R

@Composable
fun UnknownSourcesPermissionDialog() {
    val context = LocalContext.current
    var permissionGranted by remember(context) { mutableStateOf(context.packageManager.canRequestPackageInstalls()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permissionGranted = context.packageManager.canRequestPackageInstalls()
    }

    if (!permissionGranted) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    imageVector = Icons.Filled.Build,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text(stringResource(R.string.permissions_grant_title)) },
            text = {
                Text(
                    text = stringResource(R.string.install_permissions_grant_body),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                            .setData("package:${BuildConfig.APPLICATION_ID}".toUri())
                            .let(launcher::launch)
                    }
                ) {
                    Text(stringResource(R.string.action_open_settings))
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}
