package dev.voroby.client.messages.send.dto;

import org.drinkless.tdlib.TdApi;

public record TdApiMessageContent(
        long chatId,
        TdApi.InputMessageContent content
) {
}
