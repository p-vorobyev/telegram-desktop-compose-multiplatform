package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatPhoto implements UpdateNotificationListener<TdApi.UpdateChatPhoto> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateChatPhoto(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatPhoto updateChatPhoto) {
        TdApi.Chat chat = Caches.initialChatCache.get(updateChatPhoto.chatId);
        if (chat != null) {
            synchronized (chat) {
                chat.photo = updateChatPhoto.photo;
            }
            chatListUpdatesQueue.addIncomingUpdate(updateChatPhoto);
        }
    }

    @Override
    public Class<TdApi.UpdateChatPhoto> notificationType() {
        return TdApi.UpdateChatPhoto.class;
    }
}
