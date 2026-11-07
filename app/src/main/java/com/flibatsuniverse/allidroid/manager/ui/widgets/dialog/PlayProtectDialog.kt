package com.flibatsuniverse.allidroid.manager.ui.widgets.dialog

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flibatsuniverse.allidroid.manager.R

@Composable
fun PlayProtectDialog(
    onDismiss: (neverShow: Boolean) -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    var neverShow by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss(neverShow) },
        icon = {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text(stringResource(R.string.play_protect_warning_title)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.play_protect_warning_desc),
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { neverShow = !neverShow }
                        )
                        .padding(end = 16.dp)
                ) {
                    Checkbox(
                        checked = neverShow,
                        onCheckedChange = { neverShow = it },
                        interactionSource = interactionSource
                    )

                    Text(stringResource(R.string.dont_show_again))
                }
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = { onDismiss(neverShow) }) {
                Text(stringResource(R.string.action_continue))
            }
        },
        dismissButton = {
            TextButton(onClick = { context.launchPlayProtect() }) {
                Text(stringResource(R.string.play_protect_warning_open_gpp))
            }
        }
    )
}

private fun Context.launchPlayProtect() {
    runCatching {
        Intent("com.google.android.gms.settings.VERIFY_APPS_SETTINGS")
            .setPackage("com.google.android.gms")
            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .also(::startActivity)
    }
}
