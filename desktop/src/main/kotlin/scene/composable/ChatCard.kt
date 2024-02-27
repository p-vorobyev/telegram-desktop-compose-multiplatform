package scene.composable

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import common.composable.ChatIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import scene.api.deleteChat
import scene.api.markAsRead
import scene.dto.ChatPreview
import scene.dto.ChatType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatCard(chatListUpdateScope: CoroutineScope, chatPreview: ChatPreview, selectedChatId: MutableState<Long>, onClick: () -> Unit) {
    val cardModifier = sidebarWidthModifier.height(60.dp)

    ContextMenuArea(items = { contextMenuItems(chatListUpdateScope, chatPreview) }) {

        Row (verticalAlignment = Alignment.CenterVertically) {

            val color = if (selectedChatId.value == chatPreview.id) Color.LightGray else MaterialTheme.colors.surface

            Card(modifier = cardModifier, backgroundColor = color, onClick = { onClick() }) {

                Row {
                    val cleanedTitle = chatPreview.title.replace("\n", " ")
                    ChatIcon(chatPreview.photo, cleanedTitle, circleSize = 60.dp)

                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.width(370.dp)) {
                        Text(fontWeight = FontWeight.Bold, text = if (cleanedTitle.length > 30) "${cleanedTitle.substring(0, 20)}..." else cleanedTitle)
                        Text(fontSize = 14.sp, text =
                            if (chatPreview.lastMessage.length > 50)
                                "${chatPreview.lastMessage.replace("\n", " ").substring(0, 50)}..."
                            else
                                chatPreview.lastMessage.replace("\n", " ")
                        )
                    }

                    chatPreview.unreadCount?.let {
                        if (it != 0) {
                            Box(modifier = Modifier.align(Alignment.CenterVertically)
                                .size(20.dp)
                                .graphicsLayer {
                                    clip = true
                                    shape = CircleShape
                                }.background(Color.LightGray)
                            ) {
                                Text(modifier = Modifier.align(Alignment.Center), fontSize = 12.sp, text = it.toString())
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