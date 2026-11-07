
package com.flibatsuniverse.allidroid.manager.ui.screen.logs

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.utils.LogStore
import com.flibatsuniverse.allidroid.manager.utils.navigate
import com.flibatsuniverse.allidroid.manager.utils.showToast
import java.text.DateFormat
import java.util.Date

class LogsListScreen : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val ctx = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        var logs by remember { mutableStateOf(LogStore.list(ctx)) }
        var confirmWipe by remember { mutableStateOf(false) }

        if (confirmWipe) {
            AlertDialog(
                onDismissRequest = { confirmWipe = false },
                title = { Text(stringResource(R.string.logs_wipe_title)) },
                text = { Text(stringResource(R.string.logs_wipe_description)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            LogStore.deleteAll(ctx)
                            logs = LogStore.list(ctx)
                            confirmWipe = false
                            ctx.showToast(R.string.logs_deleted)
                        }
                    ) {
                        Text(stringResource(R.string.action_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmWipe = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.logs_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    },
                    actions = {
                        if (logs.isNotEmpty()) {
                            IconButton(onClick = { confirmWipe = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = stringResource(R.string.logs_delete_all)
                                )
                            }
                        }
                    }
                )
            }
        ) { pv ->
            if (logs.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv)
                ) {
                    Text(
                        text = stringResource(R.string.logs_none),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.padding(pv)) {
                    items(logs, key = { it.absolutePath }) { file ->
                        val failed = remember(file) { LogStore.hasErrors(file) }

                        androidx.compose.material3.ListItem(
                            headlineContent = {
                                Text(
                                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                        .format(Date(file.lastModified()))
                                )
                            },
                            supportingContent = {
                                Text(
                                    buildString {
                                        append(Formatter.formatShortFileSize(ctx, file.length()))
                                        if (failed) append(" • Errors")
                                    }
                                )
                            },
                            modifier = Modifier.clickable { navigator.navigate(LogFileScreen(file.absolutePath)) }
                        )
                    }
                }
            }
        }
    }
}
