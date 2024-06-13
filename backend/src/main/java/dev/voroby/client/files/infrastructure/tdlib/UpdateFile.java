package dev.voroby.client.files.infrastructure.tdlib;

import dev.voroby.client.common.file.application.NotifyChatPhotoCached;
import dev.voroby.client.files.application.NotifyMessagePhotoCached;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPhotoFile;
import dev.voroby.client.chat.common.dto.MessageId;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateFile implements UpdateNotificationListener<TdApi.UpdateFile> {

    private final NotifyChatPhotoCached notifyChatPhotoCached;

    private final NotifyMessagePhotoCached notifyMessagePhotoCached;

    public UpdateFile(NotifyChatPhotoCached notifyChatPhotoCached, NotifyMessagePhotoCached notifyMessagePhotoCached) {
        this.notifyChatPhotoCached = notifyChatPhotoCached;
        this.notifyMessagePhotoCached = notifyMessagePhotoCached;
    }

    @Override
    public void handleNotification(TdApi.UpdateFile notification) {
        TdApi.File file = notification.file;
        if (Caches.photoIdToChatIdCache.containsKey(file.id) && file.local.isDownloadingCompleted) {
            Long chatId = Caches.photoIdToChatIdCache.get(file.id);
            notifyChatPhotoCached.accept(new ChatPhotoFile(chatId, file));
        } else if (Caches.photoPreviewIdToMessageIdCache.containsKey(file.id) && file.local.isDownloadingCompleted) {
            MessageId messageId = Caches.photoPreviewIdToMessageIdCache.get(file.id);
            notifyMessagePhotoCached.accept(messageId);
        }
    }

    @Override
    public Class<TdApi.UpdateFile> notificationType() {
        return TdApi.UpdateFile.class;
    }

}