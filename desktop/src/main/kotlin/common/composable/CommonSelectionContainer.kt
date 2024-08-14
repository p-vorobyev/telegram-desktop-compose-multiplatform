package common.composable

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import common.Colors.textSelectionColor

@Composable
fun CommonSelectionContainer(content: @Composable () -> Unit) {
    val textSelectionColors = TextSelectionColors(handleColor = textSelectionColor, backgroundColor = textSelectionColor)
    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        SelectionContainer {
            content()
        }
    }
}