package sidebar.composable

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import sidebar.api.deleteChat
import sidebar.api.markAsRead
import sidebar.dto.ChatPreview
import sidebar.dto.ChatType
import java.util.*
import java.util.stream.Collectors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatCard(chatListUpdateScope: CoroutineScope, chatPreview: ChatPreview, selectedIndex: MutableState<Int>, index: Int) {
    val cardModifier = Modifier.width(width = 450.dp).height(60.dp)

    ContextMenuArea(items = { contextMenuItems(chatListUpdateScope, chatPreview) }) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            val color = if (selectedIndex.value == index) Color.LightGray else MaterialTheme.colors.surface
            var imageBitMap: ImageBitmap? = null
            chatPreview.photo?.let {
                val img: ByteArray = Base64.getDecoder().decode(it)
                imageBitMap = Bitmap.makeFromImage(Image.makeFromEncoded(img)).asComposeImageBitmap()
            }

            Card(modifier = cardModifier, backgroundColor = color, onClick = {selectedIndex.value = index}) {

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
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .graphicsLayer {
                                    clip = true
                                    shape = CircleShape
                                }.background(blueColor)
                        ) {
                            val iconText = if (title.isBlank()) "" else
                                title.split(" ").stream().limit(2).map { it.substring(0,1).uppercase() }.collect(
                                    Collectors.joining())
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
                            if (chatPreview.lastMessage.length > 50)
                                "${chatPreview.lastMessage.substring(0, 50)}..."
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

    }
}


/*
* Context menu on right click on chat in the sidebar.
*/
private fun contextMenuItems(
    chatListUpdateScope: CoroutineScope,
    chatPreview: ChatPreview
): MutableList<ContextMenuItem> {
    val items: MutableList<ContextMenuItem> = mutableListOf()
    items.add(
        ContextMenuItem("Mark as read") {
            chatListUpdateScope.launch {
                markAsRead(chatPreview.id)
            }
        }
    )
    if (chatPreview.chatType == ChatType.Private || chatPreview.chatType == ChatType.Secret) {
        items.add(
            ContextMenuItem("Delete") {
                chatListUpdateScope.launch {
                    deleteChat(chatPreview.id)
                }
            }
        )
    } else {
        items.add(
            ContextMenuItem("Leave") {
                chatListUpdateScope.launch {
                    deleteChat(chatPreview.id)
                }
            }
        )
    }
    return items
}