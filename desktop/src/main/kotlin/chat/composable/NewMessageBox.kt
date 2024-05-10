package chat.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.api.sendMessage
import chat.dto.NewMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scene.composable.blueColor
import scene.composable.greyColor
import java.time.Instant

private const val MESSAGE_MAX_LENGTH = 2000

@Composable
fun NewMessageBox(chatId: Long, inputTextDraft: MutableState<String>) {

    val lastMessageSendAttempt = remember { mutableStateOf(Instant.now()) }

    LaunchedEffect(Unit) {
        lastMessageSendAttempt.value = Instant.now()
    }

    Box(modifier = Modifier.fillMaxWidth().background(greyColor)) {
        OutlinedTextField(
            value = inputTextDraft.value,
            onValueChange = { inputTextDraft.value = it },
            modifier = Modifier.padding(start = 2.dp, end = 72.dp, bottom = 2.dp, top = 2.dp).fillMaxWidth().align(Alignment.Center).background(Color.White)
                .onPreviewKeyEvent {
                    when {
                        (inputTextDraft.value.isNotBlank() &&
                                it.isCtrlPressed && it.key == Key.Enter &&
                                lastMessageSendAttempt.value.plusSeconds(1L).isBefore(Instant.now())) -> {
                            executeSendMessage(lastMessageSendAttempt, chatId, inputTextDraft)
                            true
                        }
                        else -> false
                    }
                },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (inputTextDraft.value.length < MESSAGE_MAX_LENGTH) blueColor else Color.Red
            ),
            textStyle = TextStyle(fontSize = 14.sp)
        )
        Button(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp).align(Alignment.CenterEnd),
            colors = ButtonDefaults.buttonColors(backgroundColor = greyColor),
            enabled = inputTextDraft.value.isNotBlank() && inputTextDraft.value.length < MESSAGE_MAX_LENGTH,
            onClick = {
                if (lastMessageSendAttempt.value.plusSeconds(1L).isBefore(Instant.now())) {
                    lastMessageSendAttempt.value = Instant.now()
                    sendMessageApiCall(chatId, inputTextDraft.value)
                    inputTextDraft.value = ""
                }
            }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = blueColor,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

private fun executeSendMessage(
    lastMessageSendAttempt: MutableState<Instant>,
    chatId: Long,
    inputTextDraft: MutableState<String>
) {
    lastMessageSendAttempt.value = Instant.now()
    sendMessageApiCall(chatId, inputTextDraft.value)
    inputTextDraft.value = ""
}

private fun sendMessageApiCall(chatId: Long, inputText: String) = CoroutineScope(Dispatchers.IO).launch {
    NewMessage.TextMessage(
        chatId = chatId,
        text = inputText
    ).also { sendMessage(it) }
}