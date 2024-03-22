package dev.voroby.client.api;

import dev.voroby.client.api.util.Utils;
import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class GetPhotoPreview extends AbstractUpdates implements Function<Integer, String> {

    protected GetPhotoPreview(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
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
