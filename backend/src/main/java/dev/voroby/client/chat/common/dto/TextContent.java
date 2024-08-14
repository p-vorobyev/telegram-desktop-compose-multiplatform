package dev.voroby.client.chat.common.dto;

import java.util.Collection;

public record TextContent(
        String text,
        Collection<TextEntity> entities
) {
}
