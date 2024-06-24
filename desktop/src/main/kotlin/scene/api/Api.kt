package scene.api

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fasterxml.jackson.core.type.TypeReference
import io.ktor.client.request.*
import io.ktor.client.statement.*
import scene.dto.ChatPreview
import transport.baseUrl
import transport.clientUri
import transport.httpClient
import transport.mapper

suspend fun handleChatListUpdates(chats: SnapshotStateList<ChatPreview>) {
    val updatedPreviews: List<ChatPreview> = getChatListUpdates()
    updatedPreviews.forEach { updated ->
        var idx: Int? = null
        chats.forEachIndexed { index, chatPreview ->
            if (chatPreview.id == updated.id) {
                idx = index
                return@forEachIndexed
            }
        }
        if (chats.isNotEmpty() && idx == null && updated.order != -1L) {
            chats.add(updated)
        } else if (idx != null) {
            var chatPreview = chats[idx!!]
            chats.removeAt(idx!!)
            if (updated.order != -1L) {
                chatPreview = chatPreview.copy(title = updated.title, lastMessage = updated.lastMessage, photo = updated.photo, unreadCount = updated.unreadCount, order = updated.order)
                chats.add(idx!!, chatPreview)
            }
        }
    }
    chats.sortByDescending { it.order }
}

private suspend fun getChatListUpdates(): List<ChatPreview> {
    val json = httpClient.get("${baseUrl}/${clientUri}/chatListUpdates").bodyAsText()
    return mapper.readValue(json, object : TypeReference<List<ChatPreview>>(){})
}

suspend fun markAsRead(chatId: Long) {
    httpClient.post("${baseUrl}/${clientUri}/chat/markasread/${chatId}")
}

suspend fun deleteChat(chatId: Long) {
    httpClient.post("${baseUrl}/${clientUri}/chat/delete/${chatId}")
}

suspend fun loadChats(): List<ChatPreview> {
    val json = httpClient.get("${baseUrl}/${clientUri}/loadChats").bodyAsText()
    return mapper.readValue(json, object : TypeReference<List<ChatPreview>>() {})
}

suspend fun isChatsLoaded(): Boolean {
    return httpClient.get("${baseUrl}/${clientUri}/isChatsLoaded").bodyAsText().toBoolean()
}