package sidebar

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
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
import kotlinx.coroutines.*
import org.jetbrains.skia.Bitmap
import java.util.*
import java.util.stream.Collectors

val blueColor = Color(51, 182, 255)

val greyColor = Color(red = 230, green = 230, blue = 230)


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun Sidebar() {
    var selectedIndex by remember { mutableStateOf(-1) }

    val chatPreviews = mutableStateListOf<ChatPreview>()

    var chatLoading by remember { mutableStateOf(true) }

    var needToScrollUpSidebar by remember { mutableStateOf(false) }

    var chatSearchInput: String by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (chatLoading) {
            chatLoading = !chatsLoaded()
            delay(500)
        }
        chatPreviews.addAll(loadChats())
    }

    /*if (chatPreviews.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Loading chats...")
        }
    }*/

    val chatListUpdateScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    chatListUpdateScope.launch {
        while (true) {
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
                Column {
                    Row(modifier = Modifier.width(width = 450.dp).height(40.dp).then(Modifier.background(MaterialTheme.colors.surface)),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Chats")
                    }
                    Row (modifier = cardModifier.then(Modifier.background(MaterialTheme.colors.surface))) {
                        Icon(Icons.Rounded.Search, contentDescription = "Filter", modifier = Modifier.align(Alignment.CenterVertically))
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
                        }
                        OutlinedTextField(
                            value = chatSearchInput,
                            onValueChange = {chatSearchInput = it},
                            placeholder = {Text("Search")},
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = blueColor
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        },
        backgroundColor = greyColor
    ) {

        Row {

            LazyColumn(state = lazyListState ) {

                itemsIndexed(chatPreviews, {_, v -> v}) { index, chatPreview ->

                    if (chatSearchInput.isBlank() || chatPreview.title.contains(chatSearchInput, ignoreCase = true)) {

                        ContextMenuArea(items = { contextMenuItems(chatListUpdateScope, chatPreview) }) {

                            Row (verticalAlignment = Alignment.CenterVertically) {
                                val color = if (selectedIndex == index) Color.LightGray else MaterialTheme.colors.surface
                                var imageBitMap: ImageBitmap? = null
                                chatPreview.photo?.let {
                                    val img: ByteArray = Base64.getDecoder().decode(it)
                                    imageBitMap = Bitmap.makeFromImage(org.jetbrains.skia.Image.makeFromEncoded(img)).asComposeImageBitmap()
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
                                                    title.split(" ").stream().limit(2).map { it.substring(0,1).uppercase() }.collect(Collectors.joining())
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

                        Divider(modifier = Modifier.height(2.dp).width(1.dp))

                    }

                }

            }

            Column(
                verticalArrangement = Arrangement.Center,
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