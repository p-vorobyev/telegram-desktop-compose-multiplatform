package chat.composable

import androidx.compose.runtime.Composable
import chat.dto.Content

@Composable
fun MessageGif(content: Content.UrlContent.GifFile) {
    if (content.url.isEmpty()) {
        //show loader preview if animation is not loaded yet
        LoaderAnimation()
    } else {
        MessageVideoCard(content)
    }
}