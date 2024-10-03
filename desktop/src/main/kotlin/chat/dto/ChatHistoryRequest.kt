package chat.dto

data class ChatHistoryRequest(
    val chatId: Long,
    val fromMessageId: Long,
    val limit: Int = 100
)
