package scene.dto

data class ChatPreview(
    var id: Long = -1,
    var title: String,
    var photo: String?,
    var lastMessage: String,
    var unreadCount: Int?,
    var order: Long,
    var chatType: ChatType
)