package chat.composable

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import chat.dto.Content
import common.Colors.blockQuoteColor
import common.Colors.blueUrlColor

@OptIn(ExperimentalTextApi::class)
@Composable
fun AnnotatedMessageText(messageText: String, textEntities: Collection<Content.TextEntity>) {
    val uriHandler = LocalUriHandler.current

    val clickedOffset = remember { mutableStateOf(-1) }

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
                    addUrlAnnotation(UrlAnnotation(it.url), startIndex, endIndex)
                }

                is Content.TextEntity.Url -> {
                    addStyle(
                        style = SpanStyle(color = blueUrlColor, textDecoration = TextDecoration.Underline),
                        start = startIndex,
                        end = endIndex
                    )
                    val url = messageText.substring(startIndex, endIndex)
                    addUrlAnnotation(UrlAnnotation(url), startIndex, endIndex)
                }
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(fontSize = 14.sp),
        onClick = { offset -> clickedOffset.value = offset }
    )

    if (clickedOffset.value != -1) {
        annotatedString.getUrlAnnotations(clickedOffset.value, clickedOffset.value)
            .firstOrNull()?.let {
                uriHandler.openUri(it.item.url)
                clickedOffset.value = -1
            }
    }
}