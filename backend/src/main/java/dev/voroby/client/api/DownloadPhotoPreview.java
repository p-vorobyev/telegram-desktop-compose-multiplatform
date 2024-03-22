package dev.voroby.client.api;

import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class DownloadPhotoPreview extends AbstractUpdates implements Function<TdApi.MessagePhoto, Integer> {

    // https://core.telegram.org/api/files#image-thumbnail-types
    private final List<String> thumbnailTypes = List.of("c", "x", "m", "b");

    protected DownloadPhotoPreview(UpdatesQueues updatesQueues, @Lazy TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public Integer apply(TdApi.MessagePhoto messagePhoto) {
        TdApi.Photo photo = messagePhoto.photo;
        for (String thumbnailType: thumbnailTypes) {
            Optional<TdApi.File> photoFile = Stream.of(photo.sizes)
                    .filter(photoSize -> photoSize.type.equals(thumbnailType))
                    .map(photoSize -> photoSize.photo)
                    .findFirst();

            if (photoFile.isPresent()) {
                TdApi.File file = photoFile.get();
                if (!file.local.isDownloadingCompleted && !file.local.isDownloadingActive) {
                    telegramClient.sendAsync(new TdApi.DownloadFile(file.id, 32, 0, 0, false));
                }
                return file.id;
            }
        }

        return null;
    }
}
