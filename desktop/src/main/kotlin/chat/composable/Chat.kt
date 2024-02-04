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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.openChat
import chat.dto.ChatMessage
import common.state.ClientStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



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
fun ChatWindow(selectedIndex: MutableState<Int>,
               chatListUpdateScope: CoroutineScope = rememberCoroutineScope(),
               clientStates: ClientStates
) {
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

    LazyColumn(modifier = Modifier.background(Color.White).padding(start = 5.dp, end = 5.dp), verticalArrangement = Arrangement.Top, state = chatHistoryListState) {
        val messageCardColor = Color(0xFFE7E5E5)
        items(clientStates.chatHistory) { message ->
            Card(shape = RoundedCornerShape(12.dp), backgroundColor = messageCardColor) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Text(text = message.messageText, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}