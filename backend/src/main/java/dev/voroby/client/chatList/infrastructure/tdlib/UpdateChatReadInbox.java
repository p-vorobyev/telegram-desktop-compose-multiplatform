package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatReadInbox  implements UpdateNotificationListener<TdApi.UpdateChatReadInbox> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateChatReadInbox(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatReadInbox notification) {
        TdApi.Chat chat = Caches.initialChatCache.get(notification.chatId);
        synchronized (chat) {
            chat.unreadCount = notification.unreadCount;
            chat.lastReadInboxMessageId = notification.lastReadInboxMessageId;
        }
        chatListUpdatesQueue.addIncomingUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateChatReadInbox> notificationType() {
        return TdApi.UpdateChatReadInbox.class;
    }

}
