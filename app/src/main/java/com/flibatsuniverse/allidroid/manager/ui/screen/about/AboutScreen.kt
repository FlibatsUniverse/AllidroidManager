
package com.flibatsuniverse.allidroid.manager.ui.screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.network.dto.Contributor
import com.flibatsuniverse.allidroid.manager.network.service.HttpService
import com.flibatsuniverse.allidroid.manager.network.utils.dataOrNull
import com.flibatsuniverse.allidroid.manager.ui.components.ProjectHeader
import com.flibatsuniverse.allidroid.manager.ui.components.TextDivider
import com.flibatsuniverse.allidroid.manager.ui.screen.libraries.LibrariesScreen
import com.flibatsuniverse.allidroid.manager.ui.widgets.about.ListItem
import com.flibatsuniverse.allidroid.manager.ui.widgets.about.UserEntry
import com.flibatsuniverse.allidroid.manager.utils.Constants
import com.flibatsuniverse.allidroid.manager.utils.showToast
import io.ktor.client.request.url
import org.koin.androidx.compose.get

class AboutScreen : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.currentOrThrow
        val prefs: PreferenceManager = get()
        val http: HttpService = get()
        var tapCount by remember { mutableIntStateOf(0) }

        val contributors by produceState<List<Contributor>?>(initialValue = null) {
            value = http.request<List<Contributor>> {
                url("https://api.github.com/repos/FlibatsUniverse/AllidroidManager/contributors")
            }.dataOrNull.orEmpty().filterNot { it.login.endsWith("bot", ignoreCase = true) }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.about_manager_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                )
            }
        ) { pv ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp),
                modifier = Modifier.padding(pv)
            ) {
                item(key = "header") {
                    ProjectHeader()
                }

                item(key = "version") {
                    Text(
                        text = buildString {
                            append("v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                            if (prefs.isDeveloper) {
                                append(" • ${BuildConfig.GIT_COMMIT}")
                                if (BuildConfig.GIT_LOCAL_CHANGES || BuildConfig.GIT_LOCAL_COMMITS) append(" - Local")
                            }
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalContentColor.current.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable(enabled = !prefs.isDeveloper) {
                                tapCount++
                                when (tapCount) {
                                    3 -> ctx.showToast(R.string.msg_seven_left)
                                    5 -> ctx.showToast(R.string.msg_five_left)
                                    8 -> ctx.showToast(R.string.msg_two_left)
                                    10 -> {
                                        ctx.showToast(R.string.msg_unlocked)
                                        prefs.isDeveloper = true
                                    }
                                }
                            }
                    )
                }

                item(key = "allidroid_lead_divider") {
                    TextDivider(
                        text = stringResource(R.string.about_allidroid_lead),
                        modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)
                    )
                }

                item(key = "allidroid_lead") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UserEntry("Kuraa", "Owner & Lead dev\nAlliance Development", "ItzFlibat", isLarge = true)
                    }
                }

                item(key = "vendetta_lead_divider") {
                    TextDivider(
                        text = stringResource(R.string.about_vendetta_lead),
                        modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)
                    )
                }

                item(key = "vendetta_lead") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UserEntry("Fiery", "Lead dev\niOS", "FieryFlames")
                        UserEntry("Maisy", "Creator\nVendetta", "maisymoe", isLarge = true)
                        UserEntry("Wing", "Lead dev\nVendetta Manager", "wingio")
                    }
                }

                item(key = "thanks_divider") {
                    TextDivider(
                        text = stringResource(R.string.label_special_thanks),
                        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                    )
                }

                item(key = "thanks") {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            text = "rushii",
                            subtext = "Installer, zip library, and a portion of patching",
                            imageUrl = "https://github.com/rushiiMachine.png",
                            onClick = { uriHandler.openUri("https://github.com/rushiiMachine") }
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        ListItem(
                            text = "Xinto",
                            subtext = "for the preference manager",
                            imageUrl = "https://github.com/X1nto.png",
                            onClick = { uriHandler.openUri("https://github.com/X1nto") }
                        )
                    }
                }

                item(key = "contributors_divider") {
                    TextDivider(
                        text = stringResource(R.string.about_contributors),
                        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                    )
                }

                when (val list = contributors) {
                    null -> item(key = "contributors_loading") {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> items(list, key = { it.login }) { contributor ->
                        ListItem(
                            text = contributor.login,
                            subtext = stringResource(R.string.about_contributions, contributor.contributions.toString()),
                            imageUrl = contributor.avatarUrl,
                            onClick = { uriHandler.openUri("https://github.com/${contributor.login}") }
                        )
                    }
                }

                item(key = "links") {
                    ElevatedCard(modifier = Modifier.padding(top = 24.dp)) {
                        ListItem(
                            text = stringResource(R.string.about_source_code),
                            onClick = { uriHandler.openUri(BuildConfig.REPO_LINK) }
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        ListItem(
                            text = stringResource(R.string.title_os_libraries),
                            onClick = { navigator.push(LibrariesScreen()) }
                        )
                    }
                }
            }
        }
    }

}
