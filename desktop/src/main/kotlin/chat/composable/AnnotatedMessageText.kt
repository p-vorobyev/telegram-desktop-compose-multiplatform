package chat.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import chat.dto.Content
import common.Colors.blockQuoteColor
import common.Colors.blueUrlColor

@Composable
fun AnnotatedMessageText(messageText: String, textEntities: Collection<Content.TextEntity>) {
    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        append(messageText)
        textEntities.forEach {
            val startIndex = it.offset
            val endIndex = it.offset + it.length
            when (it) {
                is Content.TextEntity.BlockQuote -> {
                    addStyle(style = SpanStyle(background = blockQuoteColor), start = startIndex, end = endIndex)
                    addStyle(style = ParagraphStyle(textAlign = TextAlign.Start), start = startIndex, end = endIndex)
                }

                is Content.TextEntity.TextUrl -> {
                    addStyle(
                        style = SpanStyle(color = blueUrlColor, textDecoration = TextDecoration.Underline),
                        start = startIndex,
                        end = endIndex
                    )
                    addLink(
                        url = LinkAnnotation.Url(
                            url = it.url,
                            linkInteractionListener = uriHandler::openIfUrlAnnotationLink
                        ),
                        start = startIndex,
                        end = endIndex
                    )
                }

                is Content.TextEntity.Url -> {
                    addStyle(
                        style = SpanStyle(color = blueUrlColor, textDecoration = TextDecoration.Underline),
                        start = startIndex,
                        end = endIndex
                    )
                    val url = messageText.substring(startIndex, endIndex)
                    addLink(
                        url = LinkAnnotation.Url(
                            url = url,
                            linkInteractionListener = uriHandler::openIfUrlAnnotationLink
                        ),
                        start = startIndex,
                        end = endIndex
                    )
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = TextStyle(fontSize = 14.sp)
    )
}

private fun UriHandler.openIfUrlAnnotationLink(link: LinkAnnotation) {
    if (link is LinkAnnotation.Url) {
        openUri(link.url)
    }
}