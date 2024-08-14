package common.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import common.Colors.blueColor
import common.Colors.surfaceColor
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import java.util.*
import java.util.stream.Collectors

@Composable
fun ChatIcon(
    encodedChatPhoto: String?,
    chatTitle: String,
    circleSize: Dp
) {
    var imageBitMap: ImageBitmap? = null
    encodedChatPhoto?.let {
        if (it.isNotEmpty()) {
            val img: ByteArray = Base64.getDecoder().decode(it)
            imageBitMap = Bitmap.makeFromImage(Image.makeFromEncoded(img)).asComposeImageBitmap()
        }
    }
    if (imageBitMap != null) {
        Image(
            bitmap = imageBitMap!!,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(circleSize).clip(CircleShape),
            contentDescription = "",
            alignment = Alignment.Center
        )
    } else {
        Box(
            modifier = Modifier
                .size(circleSize)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                }.background(blueColor)
        ) {
            val iconText = if (chatTitle.isBlank()) "" else
                chatTitle.split(" ")
                    .stream()
                    .limit(2)
                    .map { it.substring(0,1).uppercase() }
                    .collect(Collectors.joining())
            Text(
                iconText,
                style = TextStyle(color = surfaceColor, fontSize = 20.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}