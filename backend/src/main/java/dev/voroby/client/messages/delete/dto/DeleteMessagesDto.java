package dev.voroby.client.messages.delete.dto;

public record DeleteMessagesDto(
        long chatId,
        long[] ids
) {}
