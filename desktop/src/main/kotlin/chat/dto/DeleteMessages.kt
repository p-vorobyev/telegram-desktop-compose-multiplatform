package chat.dto

data class DeleteMessages(
    val chatId: Long,
    val ids: Collection<Long>
)
