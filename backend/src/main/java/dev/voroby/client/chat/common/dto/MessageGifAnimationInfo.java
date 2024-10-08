package dev.voroby.client.chat.common.dto;

import org.drinkless.tdlib.TdApi;

public record MessageGifAnimationInfo(
        long messageId,
        long chatId,
        TdApi.MessageAnimation content
) {
}
