package sidebar.composable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import common.api.refreshChatsMemberCount
import common.state.ClientStates
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import sidebar.api.chatsLoaded
import sidebar.api.loadChats
import sidebar.dto.ChatPreview
import terminatingApp

val blueColor = Color(51, 182, 255)

val greyColor = Color(red = 230, green = 230, blue = 230)


@Composable
@Preview
fun Sidebar() {
    val chatPreviews = remember {  mutableStateListOf<ChatPreview>() }

    var chatLoading by remember { mutableStateOf(true) }

    var initChatsCompleted by remember { mutableStateOf(false) }

    val clientStates = ClientStates(chatPreviews = remember {  mutableStateListOf()}, chatsMemberCount = remember { mutableMapOf() })

    LaunchedEffect(Unit) {
        while (chatLoading && !terminatingApp.get()) {
            chatLoading = !chatsLoaded()
            delay(500)
        }
        chatPreviews.addAll(loadChats())
        initChatsCompleted = true
        async {
            refreshChatsMemberCount(clientStates)
        }
    }

    clientStates.chatPreviews = chatPreviews
    if (!initChatsCompleted) {
        ChatsLoadingDisclaimer()
    } else {
        ChatList(clientStates)
    }

}