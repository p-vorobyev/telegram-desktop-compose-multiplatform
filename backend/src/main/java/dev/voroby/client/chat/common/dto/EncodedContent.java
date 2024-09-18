package dev.voroby.client.chat.common.dto;

import com.fasterxml.jackson.annotation.JsonGetter;

public sealed interface EncodedContent {

    @JsonGetter
    EncodedContentType type();

    record Photo(String content) implements EncodedContent {

        @Override
        public EncodedContentType type() {
            return EncodedContentType.Photo;
        }
    }
}
