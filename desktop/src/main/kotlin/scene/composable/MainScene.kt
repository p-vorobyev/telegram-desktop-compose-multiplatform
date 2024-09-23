package scene.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chat.composable.ChatWindow
import chat.composable.SelectChatOffer
import common.Colors.greyColor
import common.Colors.surfaceColor
import common.States
import common.composable.CircleButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.api.handleChatListUpdates
import scene.api.markAsRead
import scene.dto.ChatPreview
import terminatingApp


val sidebarWidthModifier: Modifier = Modifier.width(450.dp)


@Composable
fun MainScene() {

    val selectedChatId: MutableState<Long> = remember { mutableStateOf(-1) }

    var needToScrollUpSidebar by remember { mutableStateOf(false) }

    val chatSearchInput: MutableState<String> = remember { mutableStateOf("") }

    val chatListUpdateScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    val filterUnreadChats: MutableState<Boolean> = remember { mutableStateOf(false) }

    chatListUpdateScope.launch {
        while (!terminatingApp.get()) {
            val chatsSizeBeforeUpdates = States.chatList.size
            var firstChatPreviewBeforeUpdates: ChatPreview? = null
            if (States.chatList.isNotEmpty()) {
                firstChatPreviewBeforeUpdates = States.chatList[0]
            }

            handleChatListUpdates(States.chatList)

            needToScrollUpSidebar = (States.chatList.size > chatsSizeBeforeUpdates) ||
                    (States.chatList.isNotEmpty() &&
                            lazyListState.firstVisibleItemIndex < 3 &&
                            firstChatPreviewBeforeUpdates != States.chatList[0])
            if (needToScrollUpSidebar) {
                lazyListState.scrollToItem(0)
            }

            delay(500)
        }
    }

    Scaffold(
        topBar = { ScaffoldTopBar(chatSearchInput, filterUnreadChats) },
        backgroundColor = greyColor
    ) {

        Row {

            Box {
                LazyColumn(state = lazyListState, modifier = sidebarWidthModifier.background(surfaceColor).fillMaxHeight()) {

                    itemsIndexed(States.chatList, { _, v -> v}) { _, chatPreview ->

                        if (chatSearchInput.value.isBlank() || chatPreview.title.contains(chatSearchInput.value, ignoreCase = true)) {
                            var hasUnread = false
                            chatPreview.unreadCount?.let {
                                if (it != 0) {
                                    hasUnread = true
                                }
                            }
                            val onChatClick = {
                                selectedChatId.value = chatPreview.id
                                States.selectedChatPreview.value = chatPreview
                            }
                            if (filterUnreadChats.value && hasUnread) {
                                ChatCard(chatPreview = chatPreview, selectedChatId = selectedChatId, onClick = onChatClick)
                                Divider(modifier = sidebarWidthModifier.height(2.dp), color = greyColor)
                            } else if (!filterUnreadChats.value) {
                                ChatCard(chatPreview = chatPreview, selectedChatId = selectedChatId, onClick = onChatClick)
                                Divider(modifier = sidebarWidthModifier.height(2.dp), color = greyColor)
                            }

                        }

                    }

                }

                if (lazyListState.firstVisibleItemIndex > 3) {
                    Row(modifier = sidebarWidthModifier) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(top = 12.dp, end = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            CircleButton(
                                imageVector = Icons.Rounded.KeyboardArrowUp ,
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
                if (selectedChatId.value == -1L && States.chatList.isNotEmpty()) {
                    SelectChatOffer()
                } else if (selectedChatId.value != -1L) {

                    var readChat by remember { mutableStateOf(-1L) }

                    Row {
                        Divider(modifier = Modifier.fillMaxHeight().width(2.dp), color = greyColor)
                        ChatWindow(chatId = selectedChatId.value, chatListUpdateScope = chatListUpdateScope)
                    }


                    States.selectedChatPreview.let {
                        it.value?.let { currentChat ->
                            currentChat.unreadCount?.let { unreadCount ->
                                if (unreadCount > 0 && readChat != currentChat.id) {
                                    readChat = currentChat.id
                                    chatListUpdateScope.launch { markAsRead(readChat) }
                                }
                            }
                        }
                    }
                }
            }

        }

    }

}