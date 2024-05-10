package scene.dto

data class ChatPreview(
    val id: Long = -1,
    val title: String,
    val photo: String?,
    val lastMessage: String,
    val unreadCount: Int?,
    val order: Long,
    val chatType: ChatType,
    val isChannel: Boolean = false,
    val canSendTextMessage: Boolean = false
)
