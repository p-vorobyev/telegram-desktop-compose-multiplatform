package dev.voroby.client.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetProfilePhoto extends AbstractUpdates implements Function<Long, String> {

    protected GetProfilePhoto(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public String apply(Long userId) {
        if (Caches.userIdToProfilePhotoCache.containsKey(userId)) {
            return Caches.userIdToProfilePhotoCache.get(userId);
        } else {
            TdApi.User user = Caches.userIdToUserCache.get(userId);
            if (user != null) {
                TdApi.ProfilePhoto profilePhoto = user.profilePhoto;
                if (profilePhoto != null) {
                    TdApi.File photoFile = profilePhoto.small;
                    if (photoFile.local.isDownloadingCompleted) {
                        String profilePhotoEncoded = Utils.fileBase64Encode(photoFile.local.path);
                        Caches.userIdToProfilePhotoCache.put(userId, profilePhotoEncoded);
                        return profilePhotoEncoded;
                    }
                }
            }
            return "";
        }
    }

}
