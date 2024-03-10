package chat.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.*
import chat.dto.ChatMessage
import common.composable.ChatIcon
import common.composable.CommonSelectionContainer
import common.composable.ScrollButton
import common.composable.ScrollDirection
import common.state.ClientStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import terminatingApp
import java.util.concurrent.locks.ReentrantLock


@Composable
fun SelectChatOffer() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(backgroundColor = Color.White, shape = RoundedCornerShape(12.dp)) {
            Text("Select chat to start messaging", modifier = Modifier.padding(12.dp))
        }
    }
}


// lock to separate initial load operation and an updates for chat (when open new chat we need to avoid updates from previous window)
val chatLock = ReentrantLock()


@Composable
fun ChatWindow(chatId: Long,
               chatListUpdateScope: CoroutineScope = rememberCoroutineScope(),
               clientStates: ClientStates
) {
    val openedId = remember { mutableStateOf(-1L) }

    val hasIncomingMessages = remember { mutableStateOf(false) }

    val fullHistoryLoaded = remember { mutableStateOf(false) } // flag tells that all messages are loaded int this chat

    val chatHistoryListState = rememberLazyListState()

    chatListUpdateScope.launch {
        if (openedId.value != chatId) {
            chatLock.lock()
            try {
                loadOpenedChatMessages(chatId = chatId, openedId = openedId, clientStates = clientStates) {
                    //scroll to the end when open chat
                    chatListUpdateScope.launch {
                        chatHistoryListState.scrollToItem(clientStates.chatHistory.size - 1)
                    }
                    fullHistoryLoaded.value = false // turn off flag when open new chat if it was activated
                }
            } finally {
                chatLock.unlock()
            }

            delay(1000)

            while (!terminatingApp.get() && openedId.value == chatId) {
                chatLock.lock()
                try {
                    if (!fullHistoryLoaded.value && chatHistoryListState.firstVisibleItemIndex == 0) {
                        preloadChatHistory(
                            chatId = chatId,
                            fullHistoryLoaded = fullHistoryLoaded,
                            clientStates = clientStates,
                            chatHistoryListState = chatHistoryListState
                        )
                    }
                    delay(500)
                    getIncomingMessages(chatId, clientStates, hasIncomingMessages)
                    delay(500)
                    getEditedMessages(clientStates)
                    delay(500)
                    handleDeletedMessages(clientStates)
                } finally {
                    chatLock.unlock()
                }
                delay(500)
            }
        }

    }

    Box {
        LazyColumn(modifier = Modifier.background(Color.White).fillMaxSize().padding(start = 5.dp, end = 12.dp), verticalArrangement = Arrangement.Bottom, state = chatHistoryListState) {
            val messageCardColor = Color(0xFFF7F4F4)
            val selectionColor = Color(0xFF95C2F0)

            items(clientStates.chatHistory) { message ->

                Row(modifier = Modifier.fillMaxWidth())  {
                    ContextMenuDataProvider(items = { messageContextMenuItems(message, chatListUpdateScope) }) {
                        ChatIcon(encodedChatPhoto = message.senderPhoto, chatTitle = message.senderInfo, circleSize = 44.dp)
                        Spacer(Modifier.width(4.dp))
                        //to enable text selection
                        CommonSelectionContainer {
                            MessageContent(message, messageCardColor, selectionColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
        if ((firstVisibleIndex + visibleItemsCount) < clientStates.chatHistory.size - 3) {
            Row(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 12.dp, end = 12.dp)) {
                ScrollButton(
                    direction = ScrollDirection.DOWN,
                    onClick = {
                        chatListUpdateScope.launch {
                            chatHistoryListState.animateScrollToItem(clientStates.chatHistory.size - 1)
                            hasIncomingMessages.value = false
                        }
                    }
                )
            }

        }


        val scrollOnTheFloor = (clientStates.chatHistory.size - (firstVisibleIndex + visibleItemsCount)) <= 2
        if (hasIncomingMessages.value && scrollOnTheFloor && clientStates.chatHistory.isNotEmpty()) {
            chatListUpdateScope.launch {
                //scroll to the end when new messages come with condition to scroll
                chatHistoryListState.scrollToItem(clientStates.chatHistory.size - 1)
                hasIncomingMessages.value = false
            }
        }

    }

}


@Composable
fun MessageContent(
    message: ChatMessage,
    messageCardColor: Color,
    selectionColor: Color
) {
    Card(shape = RoundedCornerShape(12.dp), backgroundColor = messageCardColor) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = message.senderInfo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = selectionColor)
            Spacer(Modifier.height(4.dp))
            Text(text = message.messageText, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (message.editDate.isNotEmpty()) "edited ${message.editDate}" else message.date,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


private fun messageContextMenuItems(message: ChatMessage,
                                    chatListUpdateScope: CoroutineScope
): List<ContextMenuItem> {
    val items: MutableList<ContextMenuItem> = mutableListOf()
    if (message.canBeDeletedForAllUsers || message.canBeDeletedOnlyForSelf) {
        items.add(ContextMenuItem("Delete") {
            chatListUpdateScope.launch {
                deleteMessages(message.chatId, listOf(message.id))
            }
        })
    }
    return items
}
