package dev.voroby.client.updates;

import dev.voroby.client.cache.Caches;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateUser implements UpdateNotificationListener<TdApi.UpdateUser> {

    @Override
    public void handleNotification(TdApi.UpdateUser updateUser) {
        Caches.userIdToUserCache.put(updateUser.user.id, updateUser.user);
    }

    @Override
    public Class<TdApi.UpdateUser> notificationType() {
        return TdApi.UpdateUser.class;
    }

}
