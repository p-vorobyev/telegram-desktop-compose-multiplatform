package chat.dto

data class ChatMessage(
    val id: Long,
    val chatId: Long,
    val privateChat: Boolean,
    val messageText: String,
    /*If null - no photo in message, if empty - photo exists, but not loaded yet(will come with message update)*/
    val photoPreview: String?,
    val date: String,
    val editDate: String,
    val senderInfo: String,
    val senderPhoto: String,
    val isCurrentUser: Boolean,
    val canBeDeletedForAllUsers: Boolean,
    val canBeDeletedOnlyForSelf: Boolean
)
