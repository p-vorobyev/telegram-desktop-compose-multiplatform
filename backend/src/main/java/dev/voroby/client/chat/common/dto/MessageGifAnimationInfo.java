package dev.voroby.client.chat.common.dto;

import dev.voroby.springframework.telegram.client.TdApi;

public record MessageGifAnimationInfo(
        long messageId,
        long chatId,
        TdApi.MessageAnimation content
) {
}
