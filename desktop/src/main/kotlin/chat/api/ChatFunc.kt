package chat.api

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import chat.dto.ChatHistoryRequest
import chat.dto.ChatMessage
import common.state.ClientStates
import scene.api.markAsRead

suspend fun getIncomingMessages(chatId: Long, clientStates: ClientStates, hasIncomingMessages: MutableState<Boolean>) {
    val incomingMessages: List<ChatMessage> = incomingMessages()
    if (incomingMessages.isNotEmpty()) {
        clientStates.chatHistory.addAll(incomingMessages)
        hasIncomingMessages.value = true
        markAsRead(chatId) // mark as read incoming messages
    }
}


suspend fun getEditedMessages(clientStates: ClientStates) {
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


suspend fun handleDeletedMessages(clientStates: ClientStates) {
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


suspend fun loadOpenedChatMessages(chatId: Long, openedId: MutableState<Long>, clientStates: ClientStates, openActions: () -> Unit) {
    clientStates.chatHistory.clear()
    openedId.value = chatId
    val chatHistory: List<ChatMessage> = openChat(chatId)
    clientStates.chatHistory.addAll(chatHistory)
    if (clientStates.chatHistory.isNotEmpty()) {
        openActions()
    }
}


suspend fun preloadChatHistory(chatId: Long,
                               fullHistoryLoaded: MutableState<Boolean>,
                               clientStates: ClientStates,
                               chatHistoryListState: LazyListState
) {
    if (clientStates.chatHistory.isNotEmpty()) {
        val messageId = clientStates.chatHistory[0].id
        val newChatHistory: List<ChatMessage> = loadChatHistory(ChatHistoryRequest(chatId = chatId, fromMessageId = messageId))
        if (newChatHistory.isEmpty()) {
            fullHistoryLoaded.value = true
        }
        clientStates.chatHistory.addAll(0, newChatHistory)
        chatHistoryListState.scrollToItem(newChatHistory.size)
    }
}