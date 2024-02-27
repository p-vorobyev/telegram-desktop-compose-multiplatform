package scene.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chat.composable.ChatWindow
import chat.composable.SelectChatOffer
import common.composable.ScrollButton
import common.composable.ScrollDirection
import common.state.ClientStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.api.handleSidebarUpdates
import scene.api.markAsRead
import scene.dto.ChatPreview
import terminatingApp


val sidebarWidthModifier: Modifier = Modifier.width(450.dp)


@Composable
fun MainScene(clientStates: ClientStates) {

    val selectedChatId: MutableState<Long> = remember { mutableStateOf(-1) }

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
        topBar = { ScaffoldTopBar(clientStates, chatSearchInput, filterUnreadChats) },
        backgroundColor = greyColor
    ) {

        Row {

            Box {
                LazyColumn(state = lazyListState, modifier = sidebarWidthModifier.background(MaterialTheme.colors.surface).fillMaxHeight()) {

                    itemsIndexed(clientStates.chatPreviews, { _, v -> v}) { index, chatPreview ->

                        if (chatSearchInput.value.isBlank() || chatPreview.title.contains(chatSearchInput.value, ignoreCase = true)) {
                            var hasUnread = false
                            chatPreview.unreadCount?.let {
                                if (it != 0) {
                                    hasUnread = true
                                }
                            }
                            val onChatClick = {
                                selectedChatId.value = chatPreview.id
                                clientStates.selectedChatPreview.value = chatPreview
                            }
                            if (filterUnreadChats.value && hasUnread) {
                                ChatCard(chatListUpdateScope = chatListUpdateScope, chatPreview = chatPreview, selectedChatId = selectedChatId, onClick = onChatClick)
                                Divider(modifier = sidebarWidthModifier.height(2.dp), color = greyColor)
                            } else if (!filterUnreadChats.value) {
                                ChatCard(chatListUpdateScope = chatListUpdateScope, chatPreview = chatPreview, selectedChatId = selectedChatId, onClick = onChatClick)
                                Divider(modifier = sidebarWidthModifier.height(2.dp), color = greyColor)
                            }

                        }

                    }

                }

                if (lazyListState.firstVisibleItemIndex > 3) {
                    Row(modifier = sidebarWidthModifier) {
                        Row(modifier = Modifier.fillMaxSize().padding(top = 12.dp, end = 12.dp), horizontalArrangement = Arrangement.End) {
                            ScrollButton(
                                direction = ScrollDirection.UP,
                                onClick = {
                                    chatListUpdateScope.launch {
                                        lazyListState.animateScrollToItem(0)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Column {
                if (selectedChatId.value == -1L && clientStates.chatPreviews.isNotEmpty()) {
                    SelectChatOffer()
                } else if (selectedChatId.value != -1L) {

                    var readChat by remember { mutableStateOf(-1L) }

                    Row {
                        Divider(modifier = Modifier.fillMaxHeight().width(2.dp), color = greyColor)
                        ChatWindow(chatId = selectedChatId.value, chatListUpdateScope = chatListUpdateScope, clientStates = clientStates)
                    }

                    chatListUpdateScope.launch {
                        clientStates.selectedChatPreview.let {
                            it.value?.let { currentChat ->
                                currentChat.unreadCount?.let {
                                    if (it > 0 && readChat != currentChat.id) {
                                        readChat = currentChat.id
                                        markAsRead(readChat)
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }

    }

}