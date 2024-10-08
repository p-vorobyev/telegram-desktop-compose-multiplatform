package dev.voroby.client.chat.convertChatMessage.dto;

import dev.voroby.client.chat.common.dto.ChatMessage;
import org.drinkless.tdlib.TdApi;

public record ConvertChatMessageContext(
        TdApi.Message message,
        ChatMessage chatMessage
) {
}
