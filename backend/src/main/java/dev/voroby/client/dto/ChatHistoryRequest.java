package dev.voroby.client.dto;

public record ChatHistoryRequest(
   long chatId,
   long fromMessageId,
   int offset
) {}
