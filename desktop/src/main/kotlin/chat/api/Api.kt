package chat.api

import chat.dto.ChatMessage
import chat.dto.DeleteMessages
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import transport.baseUrl
import transport.clientUri
import transport.httpClient

suspend fun openChat(chatId: Long): List<ChatMessage> {
    return httpClient.post("$baseUrl/$clientUri/chat/open/${chatId}").body()
}

suspend fun incomingMessages(): List<ChatMessage> {
    return httpClient.get("$baseUrl/$clientUri/chat/incoming").body()
}

suspend fun editedMessages(): List<ChatMessage> {
    return httpClient.get("$baseUrl/$clientUri/chat/edited").body()
}

suspend fun deletedMsgIds(): List<Long> {
    return httpClient.get("$baseUrl/$clientUri/chat/deleted").body()
}

suspend fun deleteMessages(chatId: Long, ids: Collection<Long>) {
    httpClient.post {
        url("$baseUrl/$clientUri/chat/delete")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(DeleteMessages(chatId, ids))
    }
}