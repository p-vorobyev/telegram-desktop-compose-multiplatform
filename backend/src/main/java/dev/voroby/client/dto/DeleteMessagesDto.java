package dev.voroby.client.dto;

public record DeleteMessagesDto(
        long chatId,
        long[] ids
) {}
