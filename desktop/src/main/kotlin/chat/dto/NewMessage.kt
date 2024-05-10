package chat.dto

sealed interface NewMessage {

    val type: MessageType

    val chatId: Long

    data class TextMessage(
        override val type: MessageType = MessageType.TextMessage,
        override val chatId: Long,
        val text: String
    ) : NewMessage
}

enum class MessageType { TextMessage }
