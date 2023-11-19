package dev.voroby.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatPreview(
        long id,
        String title,
        String photo,
        String lastMessage,
        long unreadCount,
        long order
) {}
