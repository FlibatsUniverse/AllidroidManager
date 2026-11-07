
package com.flibatsuniverse.allidroid.manager.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ProjectHeader(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.ic_allidroid_header),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 6.dp)
                .size(88.dp)
        )

        Text(
            text = BuildConfig.MOD_NAME,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 26.sp)
        )

        Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center
        )

        FlowRow(
            horizontalArrangement = Arrangement.Center,
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { uriHandler.openUri(BuildConfig.ORG_LINK) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_github),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = ButtonDefaults.IconSpacing)
                        .size(20.dp)
                )
                Text(stringResource(R.string.label_github))
            }

            TextButton(onClick = { uriHandler.openUri(BuildConfig.INVITE_LINK) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_discord),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = ButtonDefaults.IconSpacing)
                        .size(20.dp)
                )
                Text(stringResource(R.string.label_discord))
            }

            TextButton(onClick = { uriHandler.openUri(BuildConfig.WEBSITE_LINK) }) {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = ButtonDefaults.IconSpacing)
                        .size(20.dp)
                )
                Text(stringResource(R.string.label_website))
            }
        }
    }
}
