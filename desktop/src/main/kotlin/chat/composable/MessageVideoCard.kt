package chat.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import chat.dto.Content
import common.composable.CircleButton

@Composable
fun MessageVideoCard(content: Content.UrlContent) {
    val uriHandler = LocalUriHandler.current

    Row(modifier = Modifier.padding(all = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        CircleButton(imageVector = Icons.Rounded.PlayArrow, onClick = { uriHandler.openUri(content.url) })
        Spacer(Modifier.width(4.dp))
        Text(content.fileName)
    }
}