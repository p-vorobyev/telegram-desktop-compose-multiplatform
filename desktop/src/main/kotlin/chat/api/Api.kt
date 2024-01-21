package chat.api

import chat.dto.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import transport.baseUrl
import transport.clientUri
import transport.httpClient

suspend fun openChat(chatId: Long): List<ChatMessage> {
    return httpClient.post("$baseUrl/$clientUri/chat/open/${chatId}").body()
}