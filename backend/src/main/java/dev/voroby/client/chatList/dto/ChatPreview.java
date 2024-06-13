package dev.voroby.client.chatList.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatPreview(
        long id,
        String title,
        String photo,
        String lastMessage,
        long unreadCount,
        long order,
        ChatType chatType,
        boolean isChannel,
        boolean canSendTextMessage
) {}
