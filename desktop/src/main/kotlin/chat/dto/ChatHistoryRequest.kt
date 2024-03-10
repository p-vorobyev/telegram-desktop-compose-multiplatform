package chat.dto

data class ChatHistoryRequest(
    val chatId: Long,
    val fromMessageId: Long,
    val offset: Int = 0
)
