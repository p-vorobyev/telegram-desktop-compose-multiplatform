package dev.voroby.client.chat.common.dto;

import com.fasterxml.jackson.annotation.JsonGetter;

public sealed interface TextEntity {

    @JsonGetter
    TextEntityType type();

    record BlockQuote(
            int length,
            int offset
    ) implements TextEntity {

        @Override
        public TextEntityType type() {
            return TextEntityType.TextEntityTypeBlockQuote;
        }
    }

    record TextUrl(
            int length,
            int offset,
            String url
    ) implements TextEntity {

        @Override
        public TextEntityType type() {
            return TextEntityType.TextEntityTypeTextUrl;
        }
    }

    record Url(
            int length,
            int offset
    ) implements TextEntity {

        @Override
        public TextEntityType type() {
            return TextEntityType.TextEntityTypeUrl;
        }
    }
}
