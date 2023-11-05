package sidebar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Bitmap
import java.util.*
import java.util.stream.Collectors

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun Sidebar(chats: SnapshotStateList<ChatPreview>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
        chats.forEachIndexed { index, chatPreview ->
            Row (verticalAlignment = Alignment.CenterVertically) {
                val color = if (selectedIndex == index) Color.LightGray else MaterialTheme.colors.surface
                var imageBitMap: ImageBitmap? = null
                chatPreview.photo?.let {
                    val img: ByteArray = Base64.getDecoder().decode(it)
                    imageBitMap = Bitmap.makeFromImage(org.jetbrains.skia.Image.makeFromEncoded(img)).asComposeImageBitmap()
                }
                Card(modifier = Modifier.width(width = 400.dp).height(60.dp), backgroundColor = color, onClick = {selectedIndex= index}) {
                    Row {
                        val title = chatPreview.title.replace("\n", " ")
                        if (imageBitMap != null) {
                            Image(
                                bitmap = imageBitMap!!,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(60.dp).clip(CircleShape),
                                contentDescription = "",
                                alignment = Alignment.Center
                            )
                        } else {
                            val blueColor = Color(51, 182, 255)
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .graphicsLayer {
                                        clip = true
                                        shape = CircleShape
                                    }.background(blueColor)
                            ) {
                                val iconText = title.split(" ").stream().limit(2).map { it.substring(0,1).uppercase() }.collect(Collectors.joining())
                                Text(
                                    iconText,
                                    style = TextStyle(color = Color.White, fontSize = 20.sp),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(fontWeight = FontWeight.Bold, text = if (title.length > 30) "${title.substring(0, 20)}..." else title)
                            Text(
                                if (chatPreview.lastMessage.length > 30)
                                    "${chatPreview.lastMessage.substring(0, 20)}..."
                                else
                                    chatPreview.lastMessage
                            )
                        }
                    }
                    Box(contentAlignment = Alignment.CenterEnd) {
                        chatPreview.unreadCount?.let {
                            if (it != 0) {
                                Card(shape = RoundedCornerShape(10.dp), backgroundColor = Color.LightGray) {
                                    Text(fontSize = 14.sp, text = it.toString())
                                }
                            }
                        }
                    }
                }
            }
            Divider(modifier = Modifier.height(2.dp).width(1.dp))
        }
    }
}

data class ChatPreview(
    var id: Long = -1,
    var title: String,
    var photo: String?,
    var lastMessage: String,
    var unreadCount: Int?
)