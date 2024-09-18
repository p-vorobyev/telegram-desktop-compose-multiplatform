package chat.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import chat.dto.Content
import common.Colors.blueColor
import common.Colors.surfaceColor

@Composable
fun MessageVideoCard(content: Content.UrlContent) {
    val uriHandler = LocalUriHandler.current

    Row(modifier = Modifier.padding(all = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Button(
            modifier = Modifier.size(44.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = blueColor),
            shape = CircleShape,
            onClick = { uriHandler.openUri(content.url) }
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Play content button",
                tint = surfaceColor
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(content.fileName)
    }
}