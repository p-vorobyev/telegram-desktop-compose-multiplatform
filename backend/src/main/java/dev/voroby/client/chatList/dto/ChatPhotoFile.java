package dev.voroby.client.chatList.dto;

import dev.voroby.springframework.telegram.client.TdApi;

public record ChatPhotoFile(
        long chatId,
        TdApi.File file
) {
}
