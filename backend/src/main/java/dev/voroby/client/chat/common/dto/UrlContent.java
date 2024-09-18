package dev.voroby.client.chat.common.dto;

import com.fasterxml.jackson.annotation.JsonGetter;

public sealed interface UrlContent {

    @JsonGetter
    UrlContentType type();

    record GifFile(String url, String fileName) implements UrlContent {

        @Override
        public UrlContentType type() {
            return UrlContentType.Gif;
        }
    }
}
