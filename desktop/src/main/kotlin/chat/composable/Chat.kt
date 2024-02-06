package chat.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.openChat
import chat.dto.ChatMessage
import common.composable.ChatIcon
import common.state.ClientStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import scene.composable.blueColor


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

    Box {
        LazyColumn(modifier = Modifier.background(Color.White).fillMaxSize().padding(start = 5.dp, end = 5.dp), verticalArrangement = Arrangement.Bottom, state = chatHistoryListState) {
            val messageCardColor = Color(0xFFF7F4F4)
            val selectionColor = Color(0xFF95C2F0)
            val textSelectionColors = TextSelectionColors(handleColor = selectionColor, backgroundColor = selectionColor)

            items(clientStates.chatHistory) { message ->

                Row  {
                    ChatIcon(encodedChatPhoto = message.senderPhoto, chatTitle = message.senderInfo, circleSize = 44.dp)
                    Spacer(Modifier.width(4.dp))
                    Card(shape = RoundedCornerShape(12.dp), backgroundColor = messageCardColor) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(text = message.senderInfo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = selectionColor)
                            Spacer(Modifier.height(4.dp))
                            //to enable text selection
                            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                                SelectionContainer {
                                    Text(text = message.messageText, fontSize = 14.sp)
                                }
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
                ScrollDownButton(clientStates = clientStates, chatListUpdateScope = chatListUpdateScope, chatHistoryListState = chatHistoryListState)
            }

        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollDownButton(clientStates: ClientStates, chatListUpdateScope: CoroutineScope, chatHistoryListState: LazyListState) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer {
                clip = true
                shape = CircleShape
            }.background(blueColor).onClick {
                chatListUpdateScope.launch {
                    chatHistoryListState.animateScrollToItem(clientStates.chatHistory.size - 1)
                }
            }
    ) {
        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Down",
            modifier = Modifier.align(Alignment.Center),
            tint = Color.White
        )
    }
}