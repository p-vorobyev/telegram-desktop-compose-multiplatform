package dev.voroby.client.updates;

import dev.voroby.client.api.NotifyChatPhotoCached;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatPhotoFile;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateFile implements UpdateNotificationListener<TdApi.UpdateFile> {

    private final NotifyChatPhotoCached notifyChatPhotoCached;

    public UpdateFile(NotifyChatPhotoCached notifyChatPhotoCached) {
        this.notifyChatPhotoCached = notifyChatPhotoCached;
    }

    @Override
    public void handleNotification(TdApi.UpdateFile notification) {
        TdApi.File file = notification.file;
        if (Caches.photoIdToChatIdCache.containsKey(file.id) && file.local.isDownloadingCompleted) {
            Long chatId = Caches.photoIdToChatIdCache.get(file.id);
            notifyChatPhotoCached.accept(new ChatPhotoFile(chatId, file));
        }
    }

    @Override
    public Class<TdApi.UpdateFile> notificationType() {
        return TdApi.UpdateFile.class;
    }

}