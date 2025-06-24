package org.alessandrosinibaldi.droidemporium.adminClient.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client

@Composable
fun ClientItem(
    client: Client,
    ) {
    val nameWeight = 1f
    val emailWeight = 2f
    val phoneWeight = 2f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(nameWeight)
                .clickable { },
            text = client.displayName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(emailWeight),
            text = client.email,
            maxLines = 1,
        )
        VerticalDivider(
            thickness = 1.dp
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(phoneWeight),
            text = client.phoneNumber ?: "No phone number",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalDivider(
            thickness = 1.dp
        )

        VerticalDivider(
            thickness = 1.dp
        )


    }
}