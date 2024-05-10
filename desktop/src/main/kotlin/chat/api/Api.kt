package chat.api

import chat.dto.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import transport.baseUrl
import transport.clientUri
import transport.httpClient

suspend fun openChat(chatId: Long): List<ChatMessage> =
    httpClient.post("$baseUrl/$clientUri/chat/open/${chatId}").body()

suspend fun loadChatHistory(chatHistoryRequest: ChatHistoryRequest): List<ChatMessage> =
    httpClient.post {
        url("$baseUrl/$clientUri/chat/history")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(chatHistoryRequest)
    }.body()

suspend fun incomingMessages(): List<ChatMessage> =
    httpClient.get("$baseUrl/$clientUri/chat/incoming").body()

suspend fun editedMessages(): List<ChatMessage> =
    httpClient.get("$baseUrl/$clientUri/chat/edited").body()

suspend fun deletedMsgIds(): List<Long> =
    httpClient.get("$baseUrl/$clientUri/chat/deleted").body()

suspend fun deleteMessages(chatId: Long, ids: Collection<Long>) =
    httpClient.post {
        url("$baseUrl/$clientUri/chat/delete")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(DeleteMessages(chatId, ids))
    }

suspend fun isChannelAdmin(chatId: Long): Boolean =
    httpClient.get("$baseUrl/$clientUri/chat/channel/isAdmin/$chatId")
        .bodyAsText()
        .toBoolean()

suspend fun sendMessage(newMessage: NewMessage) =
    httpClient.post {
        url("$baseUrl/$clientUri/message/send")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(newMessage)
    }