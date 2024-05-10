package dev.voroby.client.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NewMessage.TextMessage.class, name = NewMessage.TEXT_MESSAGE)
})
public sealed interface NewMessage {

    String TEXT_MESSAGE = "TextMessage";

    record TextMessage(
            long chatId,
            String text
    ) implements NewMessage {
    }

}
