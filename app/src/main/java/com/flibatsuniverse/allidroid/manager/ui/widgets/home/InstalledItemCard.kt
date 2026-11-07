
package com.flibatsuniverse.allidroid.manager.ui.widgets.home

import android.content.pm.PackageInfo
import androidx.annotation.StringRes
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.ui.components.SegmentedButton
import com.flibatsuniverse.allidroid.manager.ui.widgets.AppIcon
import com.flibatsuniverse.allidroid.manager.utils.DiscordVersion

@Composable
fun InstalledItemCard(
    info: PackageInfo,
    prefs: PreferenceManager,
    currentVersion: DiscordVersion?,
    @StringRes installLabel: Int,
    installEnabled: Boolean,
    onInstall: () -> Unit,
    onLaunch: () -> Unit,
    onInfo: () -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val label = info.applicationInfo?.loadLabel(ctx.packageManager)?.toString() ?: prefs.appName

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AppIcon(
                    customIcon = prefs.patchIcon,
                    releaseChannel = prefs.channel,
                    modifier = Modifier.size(48.dp)
                )

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = info.packageName,
                        style = MaterialTheme.typography.labelMedium,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )

                    currentVersion?.let {
                        Text(
                            text = stringResource(R.string.version_current, it.toString()),
                            style = MaterialTheme.typography.labelLarge,
                            color = LocalContentColor.current.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Button(
                onClick = onInstall,
                enabled = installEnabled,
                modifier = Modifier.fillMaxWidth()
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            ) {
                SegmentedButton(
                    icon = Icons.Filled.OpenInNew,
                    text = stringResource(R.string.action_launch),
                    onClick = onLaunch
                )
                SegmentedButton(
                    icon = Icons.Filled.Info,
                    text = stringResource(R.string.action_info),
                    onClick = onInfo
                )
                SegmentedButton(
                    icon = Icons.Filled.Delete,
                    text = stringResource(R.string.action_uninstall),
                    onClick = onUninstall
                )
            }
        }
    }
}
