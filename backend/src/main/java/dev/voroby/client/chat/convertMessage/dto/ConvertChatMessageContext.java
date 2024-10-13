package dev.voroby.client.chat.convertMessage.dto;

import dev.voroby.client.chat.common.dto.ChatMessage;
import org.drinkless.tdlib.TdApi;

public record ConvertChatMessageContext(
        TdApi.Message message,
        ChatMessage chatMessage
) {
}
