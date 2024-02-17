package common.composable

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun CommonSelectionContainer(content: @Composable () -> Unit) {
    val selectionColor = Color(0xFF95C2F0)
    val textSelectionColors = TextSelectionColors(handleColor = selectionColor, backgroundColor = selectionColor)
    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        SelectionContainer {
            content()
        }
    }
}