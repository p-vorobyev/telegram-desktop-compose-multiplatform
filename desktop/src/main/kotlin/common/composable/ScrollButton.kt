package common.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import scene.composable.blueColor

enum class ScrollDirection {
    UP, DOWN
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollButton(direction: ScrollDirection, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer {
                clip = true
                shape = CircleShape
            }.background(blueColor)
            .onClick { onClick() }
    ) {
        Icon(
            if (direction == ScrollDirection.UP) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center),
            tint = Color.White
        )
    }
}