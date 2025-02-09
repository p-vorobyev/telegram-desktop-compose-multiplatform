package dev.voroby.client.chat.common.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.util.Utils;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetProfilePhoto implements Function<Long, String> {

    private static final String EMPTY_RESULT = "";

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
                    return getProfilePhotoEncodedOrEmpty(userId, photoFile);
                }
            }
            return EMPTY_RESULT;
        }
    }

    private String getProfilePhotoEncodedOrEmpty(long userId, TdApi.File photoFile) {
        //get consistent file info
        var optionalFile = telegramClient.send(new TdApi.GetFile(photoFile.id)).getObject();
        if (optionalFile.isPresent()) {
            photoFile = optionalFile.get();
            if (photoFile.local.isDownloadingCompleted) {
                var profilePhotoEncodedOrEmpty = Utils.fileBase64Encode(photoFile.local.path);
                Caches.userIdToProfilePhotoCache.put(userId, profilePhotoEncodedOrEmpty);
                return profilePhotoEncodedOrEmpty;
            }
        }
        return EMPTY_RESULT;
    }

}
