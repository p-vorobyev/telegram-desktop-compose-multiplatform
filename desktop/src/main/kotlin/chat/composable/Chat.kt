package chat.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.*
import chat.dto.ChatMessage
import chat.dto.Content
import chat.dto.Content.EncodedContentType.Photo
import chat.dto.Content.UrlContentType.Gif
import common.Colors
import common.States
import common.composable.ChatIcon
import common.composable.CircleButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import terminatingApp
import util.blockingIO

@Composable
fun ChatWindow(
    chatId: Long,
    chatListUpdateScope: CoroutineScope = rememberCoroutineScope()
) {
    val openedId = remember { mutableStateOf(-1L) }

    val hasIncomingMessages = remember { mutableStateOf(false) }

    val fullHistoryLoaded = remember { mutableStateOf(false) } // flag tells that all messages are loaded int this chat

    val chatHistoryListState = rememberLazyListState()

    val inputTextDraft = remember { mutableStateOf("") }

    val isChannelAdmin = remember { mutableStateOf(false) }

    chatListUpdateScope.launch {
        if (openedId.value != chatId) {
            val isChannelAdminCheck = async {
                isChannelAdmin.value = isChannelAdmin(chatId)
            }
            loadOpenedChatMessages(chatId = chatId) {
                //scroll to the end when open chat
                chatListUpdateScope.launch {
                    chatHistoryListState.scrollToItem(States.chatHistory.size - 1)
                }
                fullHistoryLoaded.value = false // turn off flag when open new chat if it was activated
            }
            isChannelAdminCheck.await()
            inputTextDraft.value = ""
            openedId.value = chatId

            delay(500)

            while (!terminatingApp.get() && openedId.value == chatId) {
                if (!fullHistoryLoaded.value && chatHistoryListState.firstVisibleItemIndex == 0) {
                    preloadChatHistory(
                        chatId = chatId,
                        fullHistoryLoaded = fullHistoryLoaded,
                        chatHistoryListState = chatHistoryListState
                    )
                }
                delay(500)
                hasIncomingMessages.value = getIncomingMessages(chatId)
                delay(500)
                getEditedMessages()
                delay(500)
                handleDeletedMessages()
                delay(500)
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            States.selectedChatPreview.value?.let {
                if (it.canSendTextMessage || isChannelAdmin.value) {
                    NewMessageBox(chatId = chatId, inputTextDraft = inputTextDraft)
                }
            }
        }
    ) {
        var chatMessagesBoxModifier: Modifier = Modifier
        States.selectedChatPreview.value?.let {
            if (it.canSendTextMessage || isChannelAdmin.value) {
                chatMessagesBoxModifier = Modifier.padding(bottom = 60.dp)
            }
        }
        Box(modifier = chatMessagesBoxModifier) {
            if (openedId.value == chatId) {
                LazyColumn(
                    modifier = Modifier.background(Colors.chatBackgroundColor).fillMaxSize().padding(start = 5.dp, end = 12.dp),
                    verticalArrangement = Arrangement.Bottom,
                    state = chatHistoryListState
                ) {

                    items(States.chatHistory, key = {it.id}) { message ->

                        ContextMenuArea(items = { messageContextMenuItems(message) } ) {
                            Row(modifier = Modifier.fillMaxWidth())  {
                                //Adds items to the hierarchy of context menu items
                                ChatIcon(encodedChatPhoto = message.senderPhoto, chatTitle = message.senderInfo, circleSize = 44.dp)
                                Spacer(Modifier.width(4.dp))
                                Column {
                                    Text(text = message.senderInfo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Colors.messageHeaderColor)
                                    Spacer(Modifier.height(4.dp))
                                    message.encodedContent?.draw()
                                    message.urlContent?.draw()
                                    MessageTextCard(message)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = chatHistoryListState
                )
            )

            val firstVisibleIndex = chatHistoryListState.firstVisibleItemIndex
            val visibleItemsCount = chatHistoryListState.layoutInfo.visibleItemsInfo.size
            if ((firstVisibleIndex + visibleItemsCount) < States.chatHistory.size) {
                Row(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 12.dp, end = 12.dp)) {
                    CircleButton(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        onClick = {
                            scrollMessagesToEnd(chatListUpdateScope, chatHistoryListState, hasIncomingMessages)
                        }
                    )
                }

            }


            val scrollOnTheFloor = (States.chatHistory.size - (firstVisibleIndex + visibleItemsCount)) <= 2
            if (hasIncomingMessages.value && scrollOnTheFloor && States.chatHistory.isNotEmpty()) {
                chatListUpdateScope.launch {
                    //scroll to the end when new messages come with condition to scroll
                    chatHistoryListState.scrollToItem(States.chatHistory.size - 1)
                    hasIncomingMessages.value = false
                }
            }

        }
    }

}

private fun scrollMessagesToEnd(
    chatListUpdateScope: CoroutineScope,
    chatHistoryListState: LazyListState,
    hasIncomingMessages: MutableState<Boolean>
) {
    chatListUpdateScope.launch {
        chatHistoryListState.animateScrollToItem(States.chatHistory.size - 1)
        hasIncomingMessages.value = false
    }
}


private fun messageContextMenuItems(message: ChatMessage): List<ContextMenuItem> {
    val items: MutableList<ContextMenuItem> = mutableListOf()
    if (message.canBeDeletedForAllUsers || message.canBeDeletedOnlyForSelf) {
        items.add(ContextMenuItem("Delete") {
            blockingIO { deleteMessages(message.chatId, listOf(message.id)) }
        })
    }
    return items
}

@Composable
private fun Content.EncodedContent.draw() =
    when (type) {
        Photo -> MessagePhoto(content)
    }

@Composable
private fun Content.UrlContent.draw() =
    when (type) {
        Gif -> MessageGif(this as Content.UrlContent.GifFile)
    }