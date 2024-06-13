package dev.voroby.client.chat.common.dto;

import dev.voroby.springframework.telegram.client.TdApi;

public record MessagePhotoInfo(
        long messageId,
        long chatId,
        TdApi.MessagePhoto content
) {
}
