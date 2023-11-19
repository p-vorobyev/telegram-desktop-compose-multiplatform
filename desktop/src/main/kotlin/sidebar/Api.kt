package sidebar

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fasterxml.jackson.core.type.TypeReference
import httpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mapper

suspend fun handleLastMessageUpdate(chats: SnapshotStateList<ChatPreview>) {
    val updatedPreviews: List<ChatPreview> = updatedPreviews()
    updatedPreviews.forEach { updated ->
        var idx: Int = -1
        chats.forEachIndexed { index, chatPreview ->
            if (chatPreview.id == updated.id) {
                idx = index
                return@forEachIndexed
            }
        }
        if (idx != -1) {
            var chatPreview = chats[idx]
            chats.removeAt(idx)
            if (updated.order != 0L) {
                chatPreview = chatPreview.copy(title = updated.title, lastMessage = updated.lastMessage, unreadCount = updated.unreadCount, order = updated.order)
                chats.add(idx, chatPreview)
            }
            chats.sortByDescending { it.order }
        }
    }
}

private suspend fun updatedPreviews(): List<ChatPreview> {
    val json = httpClient.get("http://localhost:8080/client/updateSidebar").bodyAsText()
    return mapper.readValue(json, object : TypeReference<List<ChatPreview>>(){})
}