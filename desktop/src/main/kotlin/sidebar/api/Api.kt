package sidebar.api

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fasterxml.jackson.core.type.TypeReference
import io.ktor.client.request.*
import io.ktor.client.statement.*
import sidebar.dto.ChatPreview
import transport.baseUrl
import transport.clientUri
import transport.httpClient
import transport.mapper

suspend fun handleSidebarUpdates(chats: SnapshotStateList<ChatPreview>) {
    val updatedPreviews: List<ChatPreview> = updatedPreviews()
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

private suspend fun updatedPreviews(): List<ChatPreview> {
    val json = httpClient.get("${baseUrl}/${clientUri}/updateSidebar").bodyAsText()
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

suspend fun chatsLoaded(): Boolean {
    return httpClient.get("${baseUrl}/${clientUri}/chatsLoaded").bodyAsText().toBoolean()
}

suspend fun openChat(chatId: Long) {
    httpClient.post("${baseUrl}/${clientUri}/chat/open/${chatId}")
}