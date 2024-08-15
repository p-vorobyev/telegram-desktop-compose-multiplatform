package scene.composable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import common.api.refreshChatsMemberCount
import common.States
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.api.isChatsLoaded
import scene.api.loadChats
import scene.dto.ChatPreview
import terminatingApp


@Composable
@Preview
fun InitialLoad() {
    val chatPreviews = remember {  mutableStateListOf<ChatPreview>() }

    var initialLoad by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (!initialLoad && !terminatingApp.get()) {
            initialLoad = isChatsLoaded()
            delay(100)
        }
        chatPreviews.addAll(loadChats())
        launch {
            refreshChatsMemberCount()
        }
    }

    States.chatList = chatPreviews
    if (!initialLoad) {
        LoadingDisclaimer("Loading chats...")
    } else {
        MainScene()
    }

}