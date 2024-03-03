package dev.voroby.client.dto;

public record ChatMessage(
        long id,
        long chatId,
        boolean privateChat,
        String messageText,
        String date,
        String editDate,
        String senderInfo,
        String senderPhoto,
        boolean isCurrentUser,
        boolean canBeDeletedForAllUsers,
        boolean canBeDeletedOnlyForSelf
) {}
