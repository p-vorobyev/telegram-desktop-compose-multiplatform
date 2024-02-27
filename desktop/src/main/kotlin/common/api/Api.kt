package common.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import common.state.ClientStates
import transport.baseUrl
import transport.clientUri
import transport.httpClient

suspend fun getMemberCount(chatIds: List<Long>): MutableMap<Long, Long>  {
    return httpClient.post {
        url("$baseUrl/$clientUri/chat/members")
        headers {
            header(HttpHeaders.ContentType, "application/json")
        }
        setBody(chatIds)
    }.body()
}

suspend fun refreshChatsMemberCount(clientStates: ClientStates) {
    while (true) {
        val chatIds = clientStates.chatPreviews.map { it.id }.toList()
        val memberCount: MutableMap<Long, Long> = getMemberCount(chatIds)
        memberCount.forEach { (k, v) ->
            clientStates.chatsMemberCount[k] = v
        }
        delay(5000)
    }
}