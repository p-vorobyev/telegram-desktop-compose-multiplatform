package chat.composable

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import java.util.*


@Composable
fun MessagePhoto(base64Photo: String) {
    if (base64Photo.isEmpty()) {
        // show loader preview if encoded message is not loaded yet
        LoaderAnimation()
    } else {
        val img: ByteArray = Base64.getDecoder().decode(base64Photo)
        val imageBitMap = Bitmap.makeFromImage(org.jetbrains.skia.Image.makeFromEncoded(img)).asComposeImageBitmap()
        Image(bitmap = imageBitMap, contentDescription = null)
    }
}