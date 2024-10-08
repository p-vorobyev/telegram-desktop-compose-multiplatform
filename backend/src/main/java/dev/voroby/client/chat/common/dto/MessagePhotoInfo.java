package dev.voroby.client.chat.common.dto;

import org.drinkless.tdlib.TdApi;

public record MessagePhotoInfo(
        long messageId,
        long chatId,
        TdApi.MessagePhoto content
) {
}
