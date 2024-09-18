package chat.composable

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.unit.dp
import common.Resources
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import java.nio.file.Files

private val contentLoaderCodec: Codec = contentLoaderCodec()

// https://github.com/JetBrains/compose-multiplatform/issues/153#issuecomment-864608382
@Composable
fun GifAnimation(modifier: Modifier = Modifier.padding(end = 32.dp, bottom = 32.dp)) {
    val transition = rememberInfiniteTransition()
    val frameIndex by transition.animateValue(
        initialValue = 0,
        targetValue = contentLoaderCodec.frameCount - 1,
        Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 0
                for ((index, frame) in contentLoaderCodec.framesInfo.withIndex()) {
                    index at durationMillis
                    durationMillis += frame.duration
                }
            }
        )
    )

    val bitmap = remember { Bitmap().apply { allocPixels(contentLoaderCodec.imageInfo) } }
    Canvas(modifier) {
        contentLoaderCodec.readPixels(bitmap, frameIndex)
        drawImage(bitmap.asComposeImageBitmap())
    }
}

private fun contentLoaderCodec(): Codec {
    val loaderGifBytes: ByteArray = Files.readAllBytes(Resources.loaderFile.toPath())
    val codec: Codec = Codec.makeFromData(Data.makeFromBytes(loaderGifBytes))
    return codec
}
