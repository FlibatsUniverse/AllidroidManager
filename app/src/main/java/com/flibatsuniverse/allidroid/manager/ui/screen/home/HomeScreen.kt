package com.flibatsuniverse.allidroid.manager.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.flibatsuniverse.allidroid.manager.ui.components.ProjectHeader
import com.flibatsuniverse.allidroid.manager.ui.screen.about.AboutScreen
import com.flibatsuniverse.allidroid.manager.ui.screen.logs.LogsListScreen
import com.flibatsuniverse.allidroid.manager.ui.widgets.home.Commit
import com.flibatsuniverse.allidroid.manager.ui.widgets.home.InstalledItemCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.ui.components.SegmentedButton
import com.flibatsuniverse.allidroid.manager.ui.screen.installer.InstallerScreen
import com.flibatsuniverse.allidroid.manager.ui.screen.settings.SettingsScreen
import com.flibatsuniverse.allidroid.manager.ui.viewmodel.home.HomeViewModel
import com.flibatsuniverse.allidroid.manager.ui.widgets.AppIcon
import com.flibatsuniverse.allidroid.manager.ui.widgets.dialog.BatteryOptimizationDialog
import com.flibatsuniverse.allidroid.manager.ui.widgets.dialog.StoragePermissionsDialog
import androidx.compose.ui.platform.LocalContext
import com.flibatsuniverse.allidroid.manager.utils.isPlayProtectEnabled
import com.flibatsuniverse.allidroid.manager.utils.isNetworkMetered
import com.flibatsuniverse.allidroid.manager.ui.widgets.dialog.UnknownSourcesPermissionDialog
import com.flibatsuniverse.allidroid.manager.ui.widgets.dialog.PlayProtectDialog
import com.flibatsuniverse.allidroid.manager.ui.widgets.dialog.NetworkWarningDialog
import com.flibatsuniverse.allidroid.manager.ui.widgets.home.CommitList
import com.flibatsuniverse.allidroid.manager.ui.widgets.updater.UpdateDialog
import com.flibatsuniverse.allidroid.manager.utils.Constants
import com.flibatsuniverse.allidroid.manager.utils.DiscordVersion
import com.flibatsuniverse.allidroid.manager.utils.navigate
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.get

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val prefs: PreferenceManager = get()
        val viewModel: HomeViewModel = getScreenModel()

        val installed = viewModel.installManager.current
        val currentVersion = remember(installed) {
            DiscordVersion.fromVersionCode(installed?.versionCode.toString())
        }

        val latestVersion =
            remember(prefs.discordVersion, viewModel.discordVersions, prefs.channel) {
                when {
                    prefs.discordVersion.isBlank() -> viewModel.discordVersions?.get(prefs.channel)
                    else -> DiscordVersion.fromVersionCode(prefs.discordVersion)
                }
            }

        val ctx = LocalContext.current
        var showNetworkWarning by remember { mutableStateOf(false) }
        var showPlayProtectWarning by remember { mutableStateOf(false) }

        fun startInstall() = navigator.navigate(InstallerScreen(latestVersion!!))

        fun checkPlayProtect() {
            if (prefs.showPlayProtectWarning && ctx.isPlayProtectEnabled()) showPlayProtectWarning = true
            else startInstall()
        }

        // == Dialogs == //

        if (showNetworkWarning) {
            NetworkWarningDialog(
                onConfirm = { neverShow ->
                    if (neverShow) prefs.showNetworkWarning = false
                    showNetworkWarning = false
                    checkPlayProtect()
                },
                onDismiss = { neverShow ->
                    if (neverShow) prefs.showNetworkWarning = false
                    showNetworkWarning = false
                }
            )
        }

        if (showPlayProtectWarning) {
            PlayProtectDialog(
                onDismiss = { neverShow ->
                    if (neverShow) prefs.showPlayProtectWarning = false
                    showPlayProtectWarning = false
                    startInstall()
                }
            )
        }

        StoragePermissionsDialog()
        UnknownSourcesPermissionDialog()
        BatteryOptimizationDialog()

        if (
            viewModel.showUpdateDialog &&
            viewModel.release != null &&
            !BuildConfig.DEBUG
        ) {
            var progress by remember { mutableStateOf<Float?>(null) }

            UpdateDialog(
                release = viewModel.release!!,
                isUpdating = viewModel.isUpdating,
                progress = progress,
                onDismiss = { viewModel.showUpdateDialog = false },
                onConfirm = {
                    viewModel.downloadAndInstallUpdate { newProgress -> progress = newProgress }
                }
            )
        }

        val commits = viewModel.commits.collectAsLazyPagingItems()
        val installEnabled = latestVersion != null &&
            (prefs.allowDowngrade || latestVersion >= (currentVersion ?: Constants.DUMMY_VERSION))
        val installLabel = when {
            latestVersion == null -> R.string.msg_loading
            currentVersion == null -> R.string.action_install
            currentVersion == latestVersion -> R.string.action_reinstall
            latestVersion > currentVersion -> R.string.action_update
            else -> if (prefs.allowDowngrade) R.string.msg_downgrade else R.string.msg_downgrade_disallowed
        }
        val onInstall = {
            if (prefs.showNetworkWarning && ctx.isNetworkMetered()) showNetworkWarning = true
            else checkPlayProtect()
        }

        Scaffold(
            topBar = { TitleBar() }
        ) { pv ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
            ) {
                item(key = "header") {
                    ProjectHeader()
                }

                if (installed == null) {
                    item(key = "install_button") {
                        Button(
                            onClick = onInstall,
                            enabled = installEnabled,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(installLabel),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier
                                    .basicMarquee()
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                item(key = "installations_title") {
                    SectionTitle(R.string.home_installations)
                }

                if (installed == null) {
                    item(key = "no_installations") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .alpha(0.7f)
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Text(
                                text = "=^..^=",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 38.sp)
                            )

                            Text(
                                text = stringResource(R.string.home_no_installations),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                } else {
                    item(key = "installation") {
                        InstalledItemCard(
                            info = installed,
                            prefs = prefs,
                            currentVersion = currentVersion,
                            installLabel = installLabel,
                            installEnabled = installEnabled,
                            onInstall = onInstall,
                            onLaunch = { viewModel.launchMod() },
                            onInfo = { viewModel.launchModInfo() },
                            onUninstall = { viewModel.uninstallMod() }
                        )
                    }
                }

                item(key = "commits_title") {
                    SectionTitle(R.string.home_commits)
                }

                items(
                    count = commits.itemCount,
                    key = commits.itemKey { it.sha }
                ) { i ->
                    commits[i]?.let { commit ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Commit(commit = commit)
                        }
                    }
                }

                if (commits.loadState.append is LoadState.Loading || commits.loadState.refresh is LoadState.Loading) {
                    item(key = "commits_loading") {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                if (commits.loadState.append is LoadState.Error || commits.loadState.refresh is LoadState.Error) {
                    item(key = "commits_failed") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.msg_load_fail),
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center
                            )

                            Button(onClick = { commits.retry() }) {
                                Text(stringResource(R.string.action_retry))
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SectionTitle(@StringRes text: Int) {
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 24.dp, bottom = 8.dp)
        )
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun TitleBar() {
        TopAppBar(
            title = {},
            actions = { Actions() }
        )
    }

    @Composable
    private fun Actions() {
        val navigator = LocalNavigator.currentOrThrow

        IconButton(onClick = { navigator.navigate(AboutScreen()) }) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.about_manager_title)
            )
        }
        IconButton(onClick = { navigator.navigate(LogsListScreen()) }) {
            Icon(
                imageVector = Icons.Outlined.Receipt,
                contentDescription = stringResource(R.string.action_open_logs)
            )
        }
        IconButton(onClick = { navigator.navigate(SettingsScreen()) }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.title_settings)
            )
        }
    }

}
