package dev.voroby.client.messages.send.dto;

import dev.voroby.springframework.telegram.client.TdApi;

public record TdApiMessageContent(
        long chatId,
        TdApi.InputMessageContent content
) {
}
