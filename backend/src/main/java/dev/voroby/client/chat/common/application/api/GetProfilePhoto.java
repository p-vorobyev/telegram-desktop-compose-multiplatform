package dev.voroby.client.chat.common.application.api;

import dev.voroby.client.util.Utils;
import dev.voroby.client.cache.Caches;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetProfilePhoto implements Function<Long, String> {

    private final TelegramClient telegramClient;

    public GetProfilePhoto(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
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
                    //get consistent file info
                    photoFile = telegramClient.sendSync(new TdApi.GetFile(photoFile.id));
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
