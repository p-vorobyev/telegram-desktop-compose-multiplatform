package scene.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.openChat
import chat.dto.ChatMessage
import common.state.ClientStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.api.handleSidebarUpdates
import scene.dto.ChatPreview
import terminatingApp


@Composable
fun MainScene(clientStates: ClientStates) {

    val selectedIndex: MutableState<Int> = remember { mutableStateOf(-1) }

    var needToScrollUpSidebar by remember { mutableStateOf(false) }

    val chatSearchInput: MutableState<String> = remember { mutableStateOf("") }

    val chatListUpdateScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    val filterUnreadChats: MutableState<Boolean> = remember { mutableStateOf(false) }

    chatListUpdateScope.launch {
        while (!terminatingApp.get()) {
            val chatsSizeBeforeUpdates = clientStates.chatPreviews.size
            var firstChatPreviewBeforeUpdates: ChatPreview? = null
            if (clientStates.chatPreviews.isNotEmpty()) {
                firstChatPreviewBeforeUpdates = clientStates.chatPreviews[0]
            }

            handleSidebarUpdates(clientStates.chatPreviews)

            needToScrollUpSidebar = (clientStates.chatPreviews.size > chatsSizeBeforeUpdates) ||
                    (clientStates.chatPreviews.isNotEmpty() && lazyListState.firstVisibleItemIndex < 3 && firstChatPreviewBeforeUpdates != clientStates.chatPreviews[0])
            if (needToScrollUpSidebar) {
                lazyListState.scrollToItem(0)
            }

            delay(1000)
        }
    }

    Scaffold(
        topBar = { ScaffoldTopBar(clientStates, lazyListState, chatSearchInput, filterUnreadChats) },
        backgroundColor = greyColor
    ) {

        Row {

            LazyColumn(state = lazyListState, modifier = Modifier.width(450.dp).background(MaterialTheme.colors.surface).fillMaxHeight()) {

                itemsIndexed(clientStates.chatPreviews, { _, v -> v}) { index, chatPreview ->

                    if (chatSearchInput.value.isBlank() || chatPreview.title.contains(chatSearchInput.value, ignoreCase = true)) {
                        var hasUnread = false
                        chatPreview.unreadCount?.let {
                            if (it != 0) {
                                hasUnread = true
                            }
                        }
                        if (filterUnreadChats.value && hasUnread) {
                            ChatCard(chatListUpdateScope = chatListUpdateScope, chatPreview = chatPreview, selectedIndex = selectedIndex, index = index)
                            Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                        } else if (!filterUnreadChats.value) {
                            ChatCard(chatListUpdateScope = chatListUpdateScope, chatPreview = chatPreview, selectedIndex = selectedIndex, index = index)
                            Divider(modifier = Modifier.height(2.dp).width(450.dp), color = greyColor)
                        }

                    }

                }

            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedIndex.value == -1 && clientStates.chatPreviews.isNotEmpty()) {
                    Text("Select chat to start messaging")
                } else if (selectedIndex.value != -1) {

                    var openedIdx by remember { mutableStateOf(-1L) }

                    val chatHistoryListState = rememberLazyListState()

                    chatListUpdateScope.launch {
                        clientStates.selectedChatPreview.value = clientStates.chatPreviews[selectedIndex.value]
                        val chatId = clientStates.chatPreviews[selectedIndex.value].id
                        if (openedIdx != chatId) {
                            clientStates.chatHistory.clear()
                            openedIdx = chatId
                            val chatHistory: List<ChatMessage> = openChat(chatId)
                            clientStates.chatHistory.addAll(chatHistory)
                            if (clientStates.chatHistory.isNotEmpty()) {
                                //scroll to the end when open chat
                                chatHistoryListState.scrollToItem(clientStates.chatHistory.size - 1)
                            }
                        }
                    }

                    LazyColumn(state = chatHistoryListState) {
                        items(clientStates.chatHistory) { message ->
                            Text(text = message.messageText, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }

        }

    }

}