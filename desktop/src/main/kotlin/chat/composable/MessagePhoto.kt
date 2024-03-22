package chat.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.unit.dp
import common.composable.CommonSelectionContainer
import common.composable.GifAnimation
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import java.util.*


@Composable
fun MessagePhoto(base64Photo: String, contentLoaderCodec: Codec) {
    if (base64Photo.isEmpty()) {
        // show loader preview if encoded message is not loaded yet
        CommonSelectionContainer {
            Box(modifier = Modifier.size(150.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    GifAnimation(contentLoaderCodec, modifier = Modifier.padding(end = 32.dp, bottom = 32.dp))
                }
            }
        }
    } else {
        val img: ByteArray = Base64.getDecoder().decode(base64Photo)
        val imageBitMap = Bitmap.makeFromImage(org.jetbrains.skia.Image.makeFromEncoded(img)).asComposeImageBitmap()
        CommonSelectionContainer {
            Image(bitmap = imageBitMap, contentDescription = null)
        }
    }
}