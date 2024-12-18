package dev.voroby.client.users.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.files.application.api.StartDownloadFile;
import dev.voroby.client.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.TdApi;
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
                .thenAccept(response ->
                        response.onError(Utils::logError)
                                .onSuccess(file -> cacheProfilePhotoToUserIds(user.id, file.id))
                );
    }

    private static void cacheProfilePhotoToUserIds(long userId, int fileId) {
        Caches.profilePhotoIdToUserIdCache.put(fileId, userId);
        log.debug("Downloading user profile photo to local storage: [userId: {}, photoId: {}]", userId, fileId);
    }
}
