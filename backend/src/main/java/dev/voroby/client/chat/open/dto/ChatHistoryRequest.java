package dev.voroby.client.chat.open.dto;

public record ChatHistoryRequest(
   long chatId,
   long fromMessageId,
   int offset
) {}
