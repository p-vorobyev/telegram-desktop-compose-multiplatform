package dev.voroby.client.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.tdlib.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatPhoto implements UpdateNotificationListener<TdApi.UpdateChatPhoto> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatPhoto(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatPhoto updateChatPhoto) {
        TdApi.Chat chat = Caches.initialChatCache.get(updateChatPhoto.chatId);
        if (chat != null) {
            synchronized (chat) {
                chat.photo = updateChatPhoto.photo;
            }
            updatesQueues.addIncomingSidebarUpdate(updateChatPhoto);
        }
    }

    @Override
    public Class<TdApi.UpdateChatPhoto> notificationType() {
        return TdApi.UpdateChatPhoto.class;
    }
}
