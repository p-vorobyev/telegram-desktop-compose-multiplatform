package chat.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import common.Resources
import common.composable.ChatIcon
import common.composable.CommonSelectionContainer
import common.composable.ScrollButton
import common.composable.ScrollDirection
import common.state.ClientStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import terminatingApp
import java.nio.file.Files
import java.util.concurrent.locks.ReentrantLock


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
        if (openedId.value == chatId) {
            LazyColumn(modifier = Modifier.background(Color.White).fillMaxSize().padding(start = 5.dp, end = 12.dp), verticalArrangement = Arrangement.Bottom, state = chatHistoryListState) {
                val messageCardColor = Color(0xFFF7F4F4)
                val headerColor = Color(0xFF95C2F0)

                val contentLoaderCodec: Codec = contentLoaderCodec()

                items(clientStates.chatHistory, key = {it.id}) { message ->

                    Row(modifier = Modifier.fillMaxWidth())  {
                        //Adds items to the hierarchy of context menu items
                        ContextMenuDataProvider(items = { messageContextMenuItems(message, chatListUpdateScope) }) {
                            ChatIcon(encodedChatPhoto = message.senderPhoto, chatTitle = message.senderInfo, circleSize = 44.dp)
                            Spacer(Modifier.width(4.dp))
                            CommonSelectionContainer {
                                Column {
                                    Text(text = message.senderInfo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = headerColor)
                                    Spacer(Modifier.height(4.dp))
                                    message.photoPreview?.let {
                                        MessagePhoto(it, contentLoaderCodec)
                                    }
                                    MessageTextCard(message, messageCardColor)
                                }
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


private fun contentLoaderCodec(): Codec {
    val loaderFile = Resources.resolve("content_loader.gif")
    val loaderGifBytes: ByteArray = Files.readAllBytes(loaderFile.toPath())
    val codec: Codec = Codec.makeFromData(Data.makeFromBytes(loaderGifBytes))
    return codec
}