package chat.api

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import chat.dto.ChatHistoryRequest
import chat.dto.ChatMessage
import common.state.ClientStates
import scene.api.markAsRead

suspend fun getIncomingMessages(chatId: Long, hasIncomingMessages: MutableState<Boolean>) {
    val incomingMessages: List<ChatMessage> = incomingMessages()
    if (incomingMessages.isNotEmpty()) {
        ClientStates.chatHistory.addAll(incomingMessages)
        hasIncomingMessages.value = true
        markAsRead(chatId) // mark as read incoming messages
    }
}


suspend fun getEditedMessages() {
    val editedMessages: List<ChatMessage> = editedMessages()
    editedMessages.forEach { edited ->
        var idx: Int? = null
        ClientStates.chatHistory.forEachIndexed { index, chatMessage ->
            if (edited.id == chatMessage.id) {
                idx = index
                return@forEachIndexed
            }
        }
        idx?.let {
            ClientStates.chatHistory.removeAt(it)
            ClientStates.chatHistory.add(it, edited)
        }
    }
}


suspend fun handleDeletedMessages() {
    val deletedMsgIds: List<Long> = deletedMsgIds()
    deletedMsgIds.forEach { deletedMsgId ->
        ClientStates.chatHistory.removeIf { it.id == deletedMsgId }
    }
}


suspend fun loadOpenedChatMessages(chatId: Long, openActions: () -> Unit) {
    ClientStates.chatHistory.clear()
    val chatHistory: List<ChatMessage> = openChat(chatId)
    ClientStates.chatHistory.addAll(chatHistory)
    if (ClientStates.chatHistory.isNotEmpty()) {
        openActions()
    }
}


suspend fun preloadChatHistory(chatId: Long,
                               fullHistoryLoaded: MutableState<Boolean>,
                               chatHistoryListState: LazyListState
) {
    if (ClientStates.chatHistory.isNotEmpty()) {
        val lastMessageId = ClientStates.chatHistory[0].id
        val newChatHistory: List<ChatMessage> = loadChatHistory(ChatHistoryRequest(chatId = chatId, fromMessageId = lastMessageId))
        if (newChatHistory.isEmpty()) {
            fullHistoryLoaded.value = true
        }
        ClientStates.chatHistory.addAll(0, newChatHistory)
        chatHistoryListState.scrollToItem(newChatHistory.size)
    }
}