package dev.voroby.client.chat.common.application.api;

import dev.voroby.client.common.file.application.api.StartDownloadFile;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class DownloadPhotoPreview implements Function<TdApi.MessagePhoto, Integer> {

    // https://core.telegram.org/api/files#image-thumbnail-types
    private final List<String> thumbnailTypes = List.of("c", "x", "m", "b");

    private final StartDownloadFile startDownloadFile;

    public DownloadPhotoPreview(StartDownloadFile startDownloadFile) {
        this.startDownloadFile = startDownloadFile;
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
                startDownloadFile.apply(file);
                return file.id;
            }
        }

        return null;
    }
}
