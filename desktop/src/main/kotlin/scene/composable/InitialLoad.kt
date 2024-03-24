package scene.composable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import common.api.refreshChatsMemberCount
import common.state.ClientStates
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import scene.api.chatsLoaded
import scene.api.loadChats
import scene.dto.ChatPreview
import terminatingApp

val blueColor = Color(51, 182, 255)

val greyColor = Color(red = 230, green = 230, blue = 230)


@Composable
@Preview
fun InitialLoad() {
    val chatPreviews = remember {  mutableStateListOf<ChatPreview>() }

    var chatLoading by remember { mutableStateOf(true) }

    var initChatsCompleted by remember { mutableStateOf(false) }

    val clientStates = ClientStates(
        chatPreviews = remember {  mutableStateListOf()},
        chatsMemberCount = remember { mutableMapOf() },
        chatHistory = remember { mutableStateListOf()}
    )

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
        LoadingDisclaimer("Loading chats...")
    } else {
        MainScene(clientStates)
    }

}