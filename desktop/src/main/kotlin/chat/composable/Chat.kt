package chat.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
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
import common.composable.ScrollButton
import common.composable.ScrollDirection
import common.state.ClientStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
            chatLock.lock()
            val isChannelAdminCheck = async {
                isChannelAdmin.value = isChannelAdmin(chatId)
            }
            try {
                loadOpenedChatMessages(chatId = chatId) {
                    //scroll to the end when open chat
                    chatListUpdateScope.launch {
                        chatHistoryListState.scrollToItem(ClientStates.chatHistory.size - 1)
                    }
                    fullHistoryLoaded.value = false // turn off flag when open new chat if it was activated
                }
                isChannelAdminCheck.await()
                inputTextDraft.value = ""
                openedId.value = chatId
            } finally {
                chatLock.unlock()
            }

            delay(100)

            while (!terminatingApp.get() && openedId.value == chatId) {
                chatLock.lock()
                try {
                    if (!fullHistoryLoaded.value && chatHistoryListState.firstVisibleItemIndex == 0) {
                        preloadChatHistory(
                            chatId = chatId,
                            fullHistoryLoaded = fullHistoryLoaded,
                            chatHistoryListState = chatHistoryListState
                        )
                    }
                    delay(100)
                    getIncomingMessages(chatId, hasIncomingMessages)
                    delay(100)
                    getEditedMessages()
                    delay(100)
                    handleDeletedMessages()
                } finally {
                    chatLock.unlock()
                }
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ClientStates.selectedChatPreview.value?.let {
                if (it.canSendTextMessage || isChannelAdmin.value) {
                    NewMessageBox(chatId = chatId, inputTextDraft = inputTextDraft)
                }
            }
        }
    ) {
        var chatMessagesBoxModifier: Modifier = Modifier
        ClientStates.selectedChatPreview.value?.let {
            if (it.canSendTextMessage || isChannelAdmin.value) {
                chatMessagesBoxModifier = Modifier.padding(bottom = 60.dp)
            }
        }
        Box(modifier = chatMessagesBoxModifier) {
            if (openedId.value == chatId) {
                LazyColumn(modifier = Modifier.background(Color.White).fillMaxSize().padding(start = 5.dp, end = 12.dp), verticalArrangement = Arrangement.Bottom, state = chatHistoryListState) {
                    val messageCardColor = Color(0xFFF7F4F4)
                    val headerColor = Color(0xFF95C2F0)

                    val contentLoaderCodec: Codec = contentLoaderCodec()

                    items(ClientStates.chatHistory, key = {it.id}) { message ->

                        ContextMenuArea(items = { messageContextMenuItems(message, chatListUpdateScope) } ) {
                            Row(modifier = Modifier.fillMaxWidth())  {
                                //Adds items to the hierarchy of context menu items
                                ChatIcon(encodedChatPhoto = message.senderPhoto, chatTitle = message.senderInfo, circleSize = 44.dp)
                                Spacer(Modifier.width(4.dp))
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
            if ((firstVisibleIndex + visibleItemsCount) < ClientStates.chatHistory.size) {
                Row(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 12.dp, end = 12.dp)) {
                    ScrollButton(
                        direction = ScrollDirection.DOWN,
                        onClick = {
                            chatListUpdateScope.launch {
                                chatHistoryListState.animateScrollToItem(ClientStates.chatHistory.size - 1)
                                hasIncomingMessages.value = false
                            }
                        }
                    )
                }

            }


            val scrollOnTheFloor = (ClientStates.chatHistory.size - (firstVisibleIndex + visibleItemsCount)) <= 2
            if (hasIncomingMessages.value && scrollOnTheFloor && ClientStates.chatHistory.isNotEmpty()) {
                chatListUpdateScope.launch {
                    //scroll to the end when new messages come with condition to scroll
                    chatHistoryListState.scrollToItem(ClientStates.chatHistory.size - 1)
                    hasIncomingMessages.value = false
                }
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
