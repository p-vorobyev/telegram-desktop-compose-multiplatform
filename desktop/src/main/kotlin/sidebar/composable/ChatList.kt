package sidebar.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import sidebar.api.deleteChat
import sidebar.api.handleSidebarUpdates
import sidebar.api.markAsRead
import sidebar.dto.ChatPreview
import sidebar.dto.ChatType
import terminatingApp
import java.util.*
import java.util.stream.Collectors


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatList(chatPreviewsArg: SnapshotStateList<ChatPreview>) {

    val chatPreviews = remember {  mutableStateListOf<ChatPreview>() }.apply {
        addAll(chatPreviewsArg)
    }

    var selectedIndex by remember { mutableStateOf(-1) }

    var needToScrollUpSidebar by remember { mutableStateOf(false) }

    var chatSearchInput: String by remember { mutableStateOf("") }

    val chatListUpdateScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    var filterUnreadChats by remember { mutableStateOf(false) }

    chatListUpdateScope.launch {
        while (!terminatingApp.get()) {
            val chatsSizeBeforeUpdates = chatPreviews.size
            var firstChatPreviewBeforeUpdates: ChatPreview? = null
            if (chatPreviews.isNotEmpty()) {
                firstChatPreviewBeforeUpdates = chatPreviews[0]
            }

            handleSidebarUpdates(chatPreviews)

            needToScrollUpSidebar = (chatPreviews.size > chatsSizeBeforeUpdates) ||
                    (chatPreviews.isNotEmpty() && lazyListState.firstVisibleItemIndex < 3 && firstChatPreviewBeforeUpdates != chatPreviews[0])
            if (needToScrollUpSidebar) {
                lazyListState.scrollToItem(0)
            }

            delay(1000)
        }
    }

    val cardModifier = Modifier.width(width = 450.dp).height(60.dp)

    Scaffold(
        topBar = {
            if (chatPreviews.isNotEmpty()) {
                Row {
                    val topHeaderModifier = Modifier.width(width = 450.dp).height(50.dp).background(MaterialTheme.colors.surface)
                    Column {
                        Spacer(modifier = Modifier.height(5.dp).width(450.dp).background(MaterialTheme.colors.surface))
                        Row (modifier = topHeaderModifier) {
                            OutlinedTextField(
                                value = chatSearchInput,
                                onValueChange = {chatSearchInput = it},
                                placeholder = { Text("Search") },
                                modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = blueColor
                                ),
                                singleLine = true
                            )
                        }
                        Row(
                            modifier = Modifier.width(width = 450.dp).height(30.dp).background(MaterialTheme.colors.surface),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (lazyListState.firstVisibleItemIndex > 3) {
                                Icon(
                                    Icons.Rounded.KeyboardArrowUp,
                                    contentDescription = "Up",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .background(greyColor)
                                        .onClick {
                                            chatListUpdateScope.launch {
                                                lazyListState.scrollToItem(0)
                                            }
                                        }
                                )
                                Spacer(modifier = Modifier.width(50.dp))
                            }
                            var textDecorationAll by remember { mutableStateOf(TextDecoration.Underline) }
                            var textDecorationUnread by remember { mutableStateOf(TextDecoration.None) }
                            Text("All", textDecoration = textDecorationAll, modifier = Modifier.onClick {
                                textDecorationAll = TextDecoration.Underline
                                textDecorationUnread = TextDecoration.None
                                filterUnreadChats = false
                            })
                            Spacer(modifier = Modifier.width(50.dp))
                            Text("Unread", textDecoration = textDecorationUnread, modifier = Modifier.onClick {
                                textDecorationAll = TextDecoration.None
                                textDecorationUnread = TextDecoration.Underline
                                filterUnreadChats = true
                            })
                        }
                        Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                    }
                    Column(modifier = Modifier.height(20.dp).fillMaxWidth()) {

                    }
                }
            }
        },
        backgroundColor = greyColor
    ) {

        Row {

            LazyColumn(state = lazyListState, modifier = Modifier.background(MaterialTheme.colors.surface).fillMaxHeight()) {

                itemsIndexed(chatPreviews, {_, v -> v}) { index, chatPreview ->

                    if (chatSearchInput.isBlank() || chatPreview.title.contains(chatSearchInput, ignoreCase = true)) {
                        var hasUnread = false
                        chatPreview.unreadCount?.let {
                            if (it != 0) {
                                hasUnread = true
                            }
                        }
                        if (filterUnreadChats && hasUnread) {
                            ContextMenuArea(items = { contextMenuItems(chatListUpdateScope, chatPreview) }) {
                                Row (verticalAlignment = Alignment.CenterVertically) {
                                    val color = if (selectedIndex == index) Color.LightGray else MaterialTheme.colors.surface
                                    var imageBitMap: ImageBitmap? = null
                                    chatPreview.photo?.let {
                                        val img: ByteArray = Base64.getDecoder().decode(it)
                                        imageBitMap = Bitmap.makeFromImage(Image.makeFromEncoded(img)).asComposeImageBitmap()
                                    }

                                    Card(modifier = cardModifier, backgroundColor = color, onClick = {selectedIndex = index}) {

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

                            Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                        } else if (!filterUnreadChats) {
                            ContextMenuArea(items = { contextMenuItems(chatListUpdateScope, chatPreview) }) {
                                Row (verticalAlignment = Alignment.CenterVertically) {
                                    val color = if (selectedIndex == index) Color.LightGray else MaterialTheme.colors.surface
                                    var imageBitMap: ImageBitmap? = null
                                    chatPreview.photo?.let {
                                        val img: ByteArray = Base64.getDecoder().decode(it)
                                        imageBitMap = Bitmap.makeFromImage(Image.makeFromEncoded(img)).asComposeImageBitmap()
                                    }

                                    Card(modifier = cardModifier, backgroundColor = color, onClick = {selectedIndex = index}) {

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

                            Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                        }

                    }

                }

            }

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedIndex == -1 && chatPreviews.isNotEmpty()) {
                    Text("Chat not selected")
                } else if (selectedIndex != -1) {
                    Text(chatPreviews[selectedIndex].title)
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