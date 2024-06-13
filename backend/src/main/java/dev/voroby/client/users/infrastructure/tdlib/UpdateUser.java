package dev.voroby.client.users.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.users.application.CacheUserProfilePhotoService;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UpdateUser implements UpdateNotificationListener<TdApi.UpdateUser> {

    private final CacheUserProfilePhotoService cacheUserProfilePhotoService;

    public UpdateUser(@Lazy CacheUserProfilePhotoService cacheUserProfilePhotoService) {
        this.cacheUserProfilePhotoService = cacheUserProfilePhotoService;
    }

    @Override
    public void handleNotification(TdApi.UpdateUser updateUser) {
        Caches.userIdToUserCache.put(updateUser.user.id, updateUser.user);
        cacheUserProfilePhotoService.accept(updateUser.user);
    }

    @Override
    public Class<TdApi.UpdateUser> notificationType() {
        return TdApi.UpdateUser.class;
    }

}
