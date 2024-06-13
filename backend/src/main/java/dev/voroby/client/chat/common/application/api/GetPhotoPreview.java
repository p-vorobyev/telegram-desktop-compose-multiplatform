package dev.voroby.client.chat.common.application.api;

import dev.voroby.client.util.Utils;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class GetPhotoPreview implements Function<Integer, String> {

    private final TelegramClient telegramClient;

    public GetPhotoPreview(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public String apply(Integer photoPreviewId) {
        Objects.requireNonNull(photoPreviewId);
        TdApi.File file = telegramClient.sendSync(new TdApi.GetFile(photoPreviewId));
        if (file.local.isDownloadingCompleted) {
            return Utils.fileBase64Encode(file.local.path);
        }
        return "";
    }

}
