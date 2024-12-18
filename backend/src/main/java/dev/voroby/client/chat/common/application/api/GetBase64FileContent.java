package dev.voroby.client.chat.common.application.api;

import dev.voroby.client.util.Utils;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class GetBase64FileContent implements Function<Integer, String> {

    private final TelegramClient telegramClient;

    private final String NO_CONTENT = "";

    public GetBase64FileContent(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public String apply(Integer fileId) {
        Objects.requireNonNull(fileId);
        return telegramClient.send(new TdApi.GetFile(fileId))
                .getObject()
                .map(file -> {
                    if (file.local.isDownloadingCompleted) {
                        return Utils.fileBase64Encode(file.local.path);
                    }
                    return NO_CONTENT;
                }).orElse(NO_CONTENT);
    }

}
