package dev.voroby.client.updates;

import dev.voroby.client.cache.Caches;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UpdateChatNewOrder implements UpdateNotificationListener<TdApi.UpdateChatPosition> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatNewOrder(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatPosition notification) {
        if (notification.position.list instanceof TdApi.ChatListMain) {
            Caches.mainListChatIds.add(notification.chatId);
            TdApi.Chat chat = Caches.initialChatCache.get(notification.chatId);
            synchronized (chat) {
                if (chat.positions.length == 0) {
                    chat.positions = new TdApi.ChatPosition[] {notification.position};
                } else {
                    boolean newPosition = true;
                    for (TdApi.ChatPosition chatPosition: chat.positions) {
                        if (notification.position.getConstructor() == chatPosition.getConstructor()) {
                            chatPosition.list = notification.position.list;
                            chatPosition.order = notification.position.order;
                            chatPosition.source = notification.position.source;
                            chatPosition.isPinned = notification.position.isPinned;
                            newPosition = false;
                        }
                    }
                    if (newPosition) {
                        TdApi.ChatPosition[] increasedChatPositions = Arrays.copyOf(chat.positions, chat.positions.length + 1);
                        increasedChatPositions[increasedChatPositions.length - 1] = notification.position;
                        chat.positions = increasedChatPositions;
                    }
                }
            }
            updatesQueues.addIncomingSidebarUpdate(notification);
        }
    }

    @Override
    public Class<TdApi.UpdateChatPosition> notificationType() {
        return TdApi.UpdateChatPosition.class;
    }
}
