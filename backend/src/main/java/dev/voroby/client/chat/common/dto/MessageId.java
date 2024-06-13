package dev.voroby.client.chat.common.dto;

public record MessageId(
        long chatId,
        long messageId
) {}
