package chat.api

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import chat.dto.ChatHistoryRequest
import chat.dto.ChatMessage
import common.States
import scene.api.markAsRead

suspend fun getIncomingMessages(chatId: Long, hasIncomingMessages: MutableState<Boolean>) {
    val incomingMessages: List<ChatMessage> = incomingMessages()
    if (incomingMessages.isNotEmpty()) {
        States.chatHistory.addAll(incomingMessages)
        hasIncomingMessages.value = true
        markAsRead(chatId) // mark as read incoming messages
    }
}


suspend fun getEditedMessages() {
    val editedMessages: List<ChatMessage> = editedMessages()
    editedMessages.forEach { edited ->
        var idx: Int? = null
        States.chatHistory.forEachIndexed { index, chatMessage ->
            if (edited.id == chatMessage.id) {
                idx = index
                return@forEachIndexed
            }
        }
        idx?.let {
            States.chatHistory.removeAt(it)
            States.chatHistory.add(it, edited)
        }
    }
}


suspend fun handleDeletedMessages() {
    val deletedMsgIds: List<Long> = deletedMsgIds()
    deletedMsgIds.forEach { deletedMsgId ->
        States.chatHistory.removeIf { it.id == deletedMsgId }
    }
}


suspend fun loadOpenedChatMessages(chatId: Long, openActions: () -> Unit) {
    States.chatHistory.clear()
    val chatHistory: List<ChatMessage> = openChat(chatId)
    States.chatHistory.addAll(chatHistory)
    if (States.chatHistory.isNotEmpty()) {
        openActions()
    }
}


suspend fun preloadChatHistory(chatId: Long,
                               fullHistoryLoaded: MutableState<Boolean>,
                               chatHistoryListState: LazyListState
) {
    if (States.chatHistory.isNotEmpty()) {
        val lastMessageId = States.chatHistory[0].id
        val newChatHistory: List<ChatMessage> = loadChatHistory(ChatHistoryRequest(chatId = chatId, fromMessageId = lastMessageId))
        if (newChatHistory.isEmpty()) {
            fullHistoryLoaded.value = true
        }
        States.chatHistory.addAll(0, newChatHistory)
        chatHistoryListState.scrollToItem(newChatHistory.size)
    }
}