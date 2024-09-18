package dev.voroby.client.chat.common.application.api;

import dev.voroby.client.util.Utils;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@Component
public class GetBase64FileContent implements Function<Integer, String> {

    private final TelegramClient telegramClient;

    public GetBase64FileContent(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public String apply(Integer fileId) {
        Objects.requireNonNull(fileId);
        Response<TdApi.File> fileResponse = telegramClient.send(new TdApi.GetFile(fileId));
        return ofNullable(fileResponse.object()).map(file -> {
            if (file.local.isDownloadingCompleted) {
                return Utils.fileBase64Encode(file.local.path);
            }
            return "";
        }).orElse("");
    }

}
