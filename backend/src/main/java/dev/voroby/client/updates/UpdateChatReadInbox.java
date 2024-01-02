package dev.voroby.client.updates;

import dev.voroby.client.api.AbstractUpdates;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatReadInbox  implements UpdateNotificationListener<TdApi.UpdateChatReadInbox> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatReadInbox(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatReadInbox notification) {
        TdApi.Chat chat = AbstractUpdates.initialChatCache.get(notification.chatId);
        synchronized (chat) {
            chat.unreadCount = notification.unreadCount;
            chat.lastReadInboxMessageId = notification.lastReadInboxMessageId;
        }
        updatesQueues.addIncomingSidebarUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateChatReadInbox> notificationType() {
        return TdApi.UpdateChatReadInbox.class;
    }

}
