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
import terminatingApp


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
                val incomingMessages: List<ChatMessage> = incomingMessages()
                clientStates.chatHistory.addAll(incomingMessages)
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
                        }
                    }
                )
            }

        }
    }

}
