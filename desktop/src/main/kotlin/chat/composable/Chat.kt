package chat.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.deletedMsgIds
import chat.api.editedMessages
import chat.api.incomingMessages
import chat.api.openChat
import chat.dto.ChatMessage
import common.composable.ChatIcon
import common.composable.CommonSelectionContainer
import common.composable.ScrollButton
import common.composable.ScrollDirection
import common.state.ClientStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.api.markAsRead
import terminatingApp


private suspend fun getIncomingMessages(chatId: Long, clientStates: ClientStates, hasIncomingMessages: MutableState<Boolean>) {
    val incomingMessages: List<ChatMessage> = incomingMessages()
    if (incomingMessages.isNotEmpty()) {
        clientStates.chatHistory.addAll(incomingMessages)
        hasIncomingMessages.value = true
        markAsRead(chatId) // mark as read incoming messages
    }
}


private suspend fun getEditedMessages(clientStates: ClientStates) {
    val editedMessages: List<ChatMessage> = editedMessages()
    editedMessages.forEach { edited ->
        var idx: Int? = null
        clientStates.chatHistory.forEachIndexed { index, chatMessage ->
            if (edited.id == chatMessage.id) {
                idx = index
                return@forEachIndexed
            }
        }
        idx?.let {
            clientStates.chatHistory.removeAt(it)
            clientStates.chatHistory.add(it, edited)
        }
    }
}


private suspend fun handleDeletedMessages(clientStates: ClientStates) {
    val deletedMsgIds: List<Long> = deletedMsgIds()
    deletedMsgIds.forEach { deletedMsgId ->
        var idx: Int? = null
        clientStates.chatHistory.forEachIndexed { index, chatMessage ->
            if (deletedMsgId == chatMessage.id) {
                idx = index
                return@forEachIndexed
            }
        }
        idx?.let {
            clientStates.chatHistory.removeAt(it)
        }
    }
}



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



@Composable
fun ChatWindow(chatId: Long,
               chatListUpdateScope: CoroutineScope = rememberCoroutineScope(),
               clientStates: ClientStates
) {
    var openedId by remember { mutableStateOf(-1L) }

    val hasIncomingMessages = remember { mutableStateOf(false) }

    val chatHistoryListState = rememberLazyListState()

    chatListUpdateScope.launch {

        if (openedId != chatId) {
            clientStates.chatHistory.clear()
            openedId = chatId
            val chatHistory: List<ChatMessage> = openChat(chatId)
            clientStates.chatHistory.addAll(chatHistory)
            if (clientStates.chatHistory.isNotEmpty()) {
                //scroll to the end when open chat
                chatHistoryListState.scrollToItem(clientStates.chatHistory.size - 1)
            }

            delay(1000)

            while (!terminatingApp.get() && openedId == chatId) {
                getIncomingMessages(chatId, clientStates, hasIncomingMessages)
                getEditedMessages(clientStates)
                handleDeletedMessages(clientStates)
                delay(2000)
            }
        }

    }

    Box {
        LazyColumn(modifier = Modifier.background(Color.White).fillMaxSize().padding(start = 5.dp, end = 5.dp), verticalArrangement = Arrangement.Bottom, state = chatHistoryListState) {
            val messageCardColor = Color(0xFFF7F4F4)
            val selectionColor = Color(0xFF95C2F0)

            items(clientStates.chatHistory) { message ->

                Row  {
                    ChatIcon(encodedChatPhoto = message.senderPhoto, chatTitle = message.senderInfo, circleSize = 44.dp)
                    Spacer(Modifier.width(4.dp))
                    Card(shape = RoundedCornerShape(12.dp), backgroundColor = messageCardColor) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(text = message.senderInfo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = selectionColor)
                            Spacer(Modifier.height(4.dp))
                            //to enable text selection
                            CommonSelectionContainer {
                                Text(text = message.messageText, fontSize = 14.sp)
                            }
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

                Spacer(modifier = Modifier.height(12.dp))

            }
        }

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
        if (hasIncomingMessages.value && scrollOnTheFloor) {
            chatListUpdateScope.launch {
                //scroll to the end when new messages come with condition to scroll
                chatHistoryListState.scrollToItem(clientStates.chatHistory.size - 1)
                hasIncomingMessages.value = false
            }
        }

    }

}
