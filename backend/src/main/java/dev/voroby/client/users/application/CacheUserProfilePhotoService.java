package dev.voroby.client.users.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.files.application.api.StartDownloadFile;
import dev.voroby.client.util.Utils;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service @Slf4j
public class CacheUserProfilePhotoService implements Consumer<TdApi.User> {

    private final StartDownloadFile startDownloadFile;

    public CacheUserProfilePhotoService(StartDownloadFile startDownloadFile) {
        this.startDownloadFile = startDownloadFile;
    }

    @Override
    public void accept(TdApi.User user) {
        if (user.profilePhoto != null) {
            TdApi.File file = user.profilePhoto.small;
            if (!Caches.profilePhotoIdToUserIdCache.containsKey(file.id)) {
                TdApi.LocalFile localFile = file.local;
                if (localFile.isDownloadingCompleted) {
                    Caches.profilePhotoIdToUserIdCache.put(file.id, user.id);
                    return;
                }
                if (!localFile.isDownloadingActive && localFile.canBeDownloaded) {
                    downloadProfilePhotoAsync(user, file);
                }
            }
        }
    }

    private void downloadProfilePhotoAsync(TdApi.User user, TdApi.File smallPhotoFile) {
        startDownloadFile.apply(smallPhotoFile)
                .thenAccept(response -> startDownloadCallback(user, response));
    }

    private void startDownloadCallback(TdApi.User user, Response<TdApi.File> response) {
        if (response.error() != null) {
            Utils.logError(response.error());
            return;
        }
        var file = response.object();
        Caches.profilePhotoIdToUserIdCache.put(file.id, user.id);
        log.debug("Downloading user profile photo to local storage: [userId: {}, photoId: {}]",
                user.id, file.id);
    }
}
