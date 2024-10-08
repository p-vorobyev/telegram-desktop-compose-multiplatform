package dev.voroby.client.chatList.dto;

import org.drinkless.tdlib.TdApi;

public record ChatPhotoFile(
        long chatId,
        TdApi.File file
) {
}
