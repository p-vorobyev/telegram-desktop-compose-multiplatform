package dev.voroby.client.updates;

import dev.voroby.client.api.util.Utils;
import dev.voroby.client.cache.Caches;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Slf4j
public class UpdateUser implements UpdateNotificationListener<TdApi.UpdateUser> {

    private final TelegramClient telegramClient;

    public UpdateUser(@Lazy TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleNotification(TdApi.UpdateUser updateUser) {
        Caches.userIdToUserCache.put(updateUser.user.id, updateUser.user);
        TdApi.ProfilePhoto profilePhoto = updateUser.user.profilePhoto;
        if (profilePhoto != null) {
            TdApi.File smallPhotoFile = profilePhoto.small;
            if (!Caches.profilePhotoIdToUserIdCache.containsKey(smallPhotoFile.id)) {
                if (smallPhotoFile.local.isDownloadingCompleted) {
                    cacheProfilePhotoIdToUserId(smallPhotoFile.id, updateUser.user.id);
                } else {
                    downloadProfilePhotoAsync(updateUser, smallPhotoFile);
                }
            }
        }
    }

    private void downloadProfilePhotoAsync(TdApi.UpdateUser updateUser, TdApi.File smallPhotoFile) {
        telegramClient.sendWithCallback(new TdApi.DownloadFile(smallPhotoFile.id, 32, 0, 0, false), (file, error) -> {
            if (error == null) {
                cacheProfilePhotoIdToUserId(smallPhotoFile.id, updateUser.user.id);
                log.debug("Download user profile photo to local storage: [userId: {}, photoId: {}]", updateUser.user.id, smallPhotoFile.id);
            } else {
                Utils.logError(error);
            }
        });
    }

    private void cacheProfilePhotoIdToUserId(int photoId, long userId) {
        Caches.profilePhotoIdToUserIdCache.put(photoId, userId);
    }

    @Override
    public Class<TdApi.UpdateUser> notificationType() {
        return TdApi.UpdateUser.class;
    }

}
