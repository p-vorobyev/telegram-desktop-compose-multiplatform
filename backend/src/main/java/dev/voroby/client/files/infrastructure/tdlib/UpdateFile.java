package dev.voroby.client.files.infrastructure.tdlib;

import dev.voroby.client.files.application.NotifyChatPhotoCached;
import dev.voroby.client.files.application.NotifyMessageContentCached;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPhotoFile;
import dev.voroby.client.chat.common.dto.MessageId;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateFile implements UpdateNotificationListener<TdApi.UpdateFile> {

    private final NotifyChatPhotoCached notifyChatPhotoCached;

    private final NotifyMessageContentCached notifyMessageContentCached;

    public UpdateFile(NotifyChatPhotoCached notifyChatPhotoCached, NotifyMessageContentCached notifyMessageContentCached) {
        this.notifyChatPhotoCached = notifyChatPhotoCached;
        this.notifyMessageContentCached = notifyMessageContentCached;
    }

    @Override
    public void handleNotification(TdApi.UpdateFile notification) {
        TdApi.File file = notification.file;
        if (file.local.isDownloadingCompleted) {
            if (Caches.photoIdToChatIdCache.containsKey(file.id)) {
                notifyChatPhotoCached(file);
            } else if (Caches.photoPreviewIdToMessageIdCache.containsKey(file.id)) {
                MessageId messageId = Caches.photoPreviewIdToMessageIdCache.get(file.id);
                notifyMessageContentCached(messageId);
            } else if (Caches.gifAnimationIdToMessageIdCache.containsKey(file.id)) {
                MessageId messageId = Caches.gifAnimationIdToMessageIdCache.get(file.id);
                notifyMessageContentCached(messageId);
            }
        }
    }

    @Override
    public Class<TdApi.UpdateFile> notificationType() {
        return TdApi.UpdateFile.class;
    }

    private void notifyMessageContentCached(MessageId messageId) {
        notifyMessageContentCached.accept(messageId);
    }

    private void notifyChatPhotoCached(TdApi.File file) {
        Long chatId = Caches.photoIdToChatIdCache.get(file.id);
        notifyChatPhotoCached.accept(new ChatPhotoFile(chatId, file));
    }

}