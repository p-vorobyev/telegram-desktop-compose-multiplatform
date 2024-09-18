package chat.dto

data class ChatMessage(
    val id: Long,
    val chatId: Long,
    val privateChat: Boolean,
    val textContent: Content.TextContent,
    val encodedContent: Content.EncodedContent?,
    val urlContent: Content.UrlContent?,
    val date: String,
    val editDate: String,
    val senderInfo: String,
    val senderPhoto: String,
    val isCurrentUser: Boolean,
    val canBeDeletedForAllUsers: Boolean,
    val canBeDeletedOnlyForSelf: Boolean
)
