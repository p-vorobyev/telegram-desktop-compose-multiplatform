package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatTitle implements UpdateNotificationListener<TdApi.UpdateChatTitle> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateChatTitle(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatTitle updateChatTitle) {
        TdApi.Chat chat = Caches.initialChatCache.get(updateChatTitle.chatId);
        synchronized (chat) {
            chat.title = updateChatTitle.title;
        }
        chatListUpdatesQueue.addIncomingUpdate(updateChatTitle);
    }

    @Override
    public Class<TdApi.UpdateChatTitle> notificationType() {
        return TdApi.UpdateChatTitle.class;
    }

}
