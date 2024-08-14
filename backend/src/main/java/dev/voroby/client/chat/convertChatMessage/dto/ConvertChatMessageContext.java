package dev.voroby.client.chat.convertChatMessage.dto;

import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.springframework.telegram.client.TdApi;

public record ConvertChatMessageContext(
        TdApi.Message message,
        ChatMessage chatMessage
) {
}
